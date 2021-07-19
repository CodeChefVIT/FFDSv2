import express from 'express';
import controller from '../controllers/conversation';
import auth from '../middleware/auth'
const router = express.Router();

//new conv

router.post("/", controller.createNewConversation);

//get conv of a user

router.get("/", auth, controller.getConversationOfUser);

// get conv includes two userId

router.get("/find/:userId", auth, controller.getPairConversation);

export = router;