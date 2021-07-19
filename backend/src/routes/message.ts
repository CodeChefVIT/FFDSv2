import express from 'express';
import controller from '../controllers/message';

const router = express.Router();

//new message

router.post("/", controller.saveMessage);

//get conversation messages

router.get("/:conversationId", controller.getMessages);

//get last message

router.get("/last/:conversationId", controller.getLastMessage);

export = router;    