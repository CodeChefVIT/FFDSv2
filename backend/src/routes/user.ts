import express from 'express';
import controller from '../controllers/user';
import auth from '../middleware/auth'

const router = express.Router();

router.get('/validate',auth, controller.validateToken);

router.get('/all', controller.getAllUsers);

router.get('/profile', auth, controller.getProfile);

router.get('/slot', auth, controller.slotUploader);

router.get('/details', controller.getUser);

router.post('/register', controller.register);

router.post('/login',controller.login);

router.post('/email',controller.sendEmailLink);

router.get('/verification', controller.verifyUser);

router.put('/update',auth,controller.updateUser);

export = router;