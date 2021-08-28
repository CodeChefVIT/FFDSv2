import express from 'express';
import controller from '../controllers/user';
import auth from '../middleware/auth'

const router = express.Router();

router.get('/validate',auth, controller.validateToken);

router.get('/all', controller.getAllUsers);

router.get('/profile', auth, controller.getProfile);

router.get('/feed', auth, controller.getFeed);

router.post('/slot', controller.slotUploader);

router.get('/details', controller.getUser);

router.post('/register', controller.register);

router.post('/login',controller.login);

router.post('/email/verify',controller.sendEmailVerification);

router.post('/email/password',controller.sendEmailPassword);

router.post('/reset', controller.updatePassword);

router.get('/verification', controller.verifyUser);

router.get('/passwordReset', controller.passwordReset);

router.put('/update',auth,controller.updateUser);

router.put('/reject/:userId',auth,controller.rejectMatch);

router.post('/image',auth, controller.imageUpload);

export = router;