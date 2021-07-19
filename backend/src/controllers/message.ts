import {Request, Response, NextFunction} from 'express';
import logging from '../config/logging';
import Message from '../models/Message';

const NAMESPACE="Message Controller"

const saveMessage = async(req: Request, res: Response, next: NextFunction) =>{
    logging.info(NAMESPACE,"Saving the message to DB")
    let {conversationId, senderId, text} = req.body;
    if(conversationId===undefined || senderId===undefined || text===undefined){
        return res.status(400).send({
            "err":true,
            "message":"Data Missing, Could not send message"
        })
    }
    const newMessage = new Message(req.body);

    try {
        const savedMessage = await newMessage.save();
        return res.status(200).json(savedMessage);
    } catch (err) {
        return res.status(500).json(err);
    }
}

const getMessages = async(req: Request, res: Response, next: NextFunction) =>{
    try {
        const messages = await Message.find({
          conversationId: req.params.conversationId,
        });
        return res.status(200).json(messages);
      } catch (err) {
        return res.status(500).json(err);
      }
}

const getLastMessage = async(req: Request, res: Response, next: NextFunction) =>{
    try {
        const message = await Message.findOne({
          conversationId: req.params.conversationId,
        }).sort({_id:-1});
        return res.status(200).json(message);
      } catch (err) {
        return res.status(500).json(err);
      }
}

export default {saveMessage, getMessages, getLastMessage};