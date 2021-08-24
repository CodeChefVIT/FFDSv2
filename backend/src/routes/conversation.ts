import express from 'express';
import controller from '../controllers/conversation';
import auth from '../middleware/auth'
const router = express.Router();


router.post("/", auth, controller.createNewConversation);

router.get("/", auth, controller.getConversationOfUser);

router.get("/find/:userId", auth, controller.getPairConversation);

router.get("/block/:userId", auth, controller.block);

router.get("/unblock/:userId", auth, controller.unBlock);

export = router;