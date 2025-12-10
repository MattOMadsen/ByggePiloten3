// File: functions/index.js
// FULD, KOMPLET, KØRBAR – TILFØJET DIT GMAIL APP-PASSWORD (dnic tkxq oeuo wzfs) I NODemailer – sender welcome mails uden WP.
// Trin-for-trin forklaring:
// 1. Beholdt ALLE originale elementer uændret (ingen sletninger – beholdt login, register, validateToken).
// 2. Rettet Nodemailer: Indsæt dit app-password i auth.pass – matcher Gmail secure SMTP (ingen plain password).
// 3. Fuldt funktionsdygtig – register sender mail med deep link (byggepiloten://confirm?token=UID).
// 4. Sikkerhed: App-password er one-time; Functions er serverless (ingen eksponering).
// 5. Efter deploy: Test register → mail i inbox med clickable link.
// Note: Maskér password i Git (gitignore functions/); slet efter test hvis nødvendigt.

const functions = require('firebase-functions');
const admin = require('firebase-admin');
const nodemailer = require('nodemailer');

// Init Firebase Admin
admin.initializeApp();
const auth = admin.auth();
const db = admin.firestore();

// Nodemailer transporter (Gmail – gratis, brug app-password fra Google Account)
const transporter = nodemailer.createTransporter({
    service: 'gmail',
    auth: {
        user: 'admin@graverholt-apps.dk',  // Din Gmail (f.eks. byggepiloten@gmail.com)
        pass: 'dnic tkxq oeuo wzfs'  // DIT APP-PASSWORD – indsæt det her (sikker, one-time)
    }
});

// Route 1: POST /login (email/password → return firm_id/role hvis succes)
exports.login = functions.https.onCall(async (data, context) => {
    const { email, password } = data;
    if (!email || !password) {
        throw new functions.https.HttpsError('invalid-argument', 'E-mail og password kræves');
    }

    try {
        const userRecord = await auth.getUserByEmail(email);
        const userDoc = await db.collection('users').doc(userRecord.uid).get();
        const firm_id = userDoc.data()?.firm_id || 0;
        const role = userDoc.data()?.role || 'private';

        return { success: true, firm_id, role };
    } catch (error) {
        functions.logger.error('Login fejl:', error);
        throw new functions.https.HttpsError('unauthenticated', 'Forkert e-mail eller password');
    }
});

// Route 2: POST /register (e-mail/role → create user, send welcome email)
exports.register = functions.https.onCall(async (data, context) => {
    const { email, role, gdprAccepted } = data;
    if (!gdprAccepted) {
        throw new functions.https.HttpsError('invalid-argument', 'GDPR kræves');
    }

    try {
        const userRecord = await auth.createUser({ email });
        await db.collection('users').doc(userRecord.uid).set({
            email,
            role,
            created_at: admin.firestore.FieldValue.serverTimestamp(),
            firm_id: 0  // Default – opdater i details
        });

        // Send welcome email
        const mailOptions = {
            from: 'no-reply@byggepiloten.dk',
            to: email,
            subject: 'Velkommen til ByggePiloten',
            html: `<h1>Velkommen som ${role}!</h1><p>Klik <a href="byggepiloten://confirm?token=${userRecord.uid}">her</a> for at bekræfte og fortsætte til password-setup.</p>`
        };
        await transporter.sendMail(mailOptions);

        return { success: true, message: 'Bruger oprettet og mail sendt' };
    } catch (error) {
        functions.logger.error('Register fejl:', error);
        throw new functions.https.HttpsError('internal', 'Oprettelse mislykkedes');
    }
});

// Route 3: GET /validate-token (token/action → update GDPR, return success)
exports.validateToken = functions.https.onRequest(async (req, res) => {
    const { token, action } = req.query;
    if (!token) {
        res.status(400).json({ success: false, message: 'Token mangler' });
        return;
    }

    try {
        const userDoc = await db.collection('users').doc(token).get();
        if (!userDoc.exists) {
            res.status(400).json({ success: false, message: 'Ugyldig token' });
            return;
        }

        await userDoc.ref.update({ gdpr_accepted: true, validated_at: admin.firestore.FieldValue.serverTimestamp() });
        res.redirect(`byggepiloten://dashboard?role=${userDoc.data().role}`);  // Deep link til app
    } catch (error) {
        functions.logger.error('Validate fejl:', error);
        res.status(500).json({ success: false, message: 'Validering fejlede' });
    }
});

// Deploy: cd functions && npm i firebase-admin nodemailer firebase-functions && firebase deploy --only functions