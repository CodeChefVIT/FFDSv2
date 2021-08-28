import {Request, Response, NextFunction} from 'express';
import logging from '../config/logging';
import Message from '../models/Message';
import Conversation from '../models/Conversation';

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

    let conversation = await Conversation.findById(conversationId).catch((err:any)=>{
      return res.status(500).json(err);
    })
    if(!conversation){
      return res.status(404).json({
        "err": true,
        "message": "No Conversation found"
        }
      );
    }
    else if(conversation.matched && !conversation.blocked){
      const newMessage = new Message(req.body);
  
      try {
          const savedMessage = await newMessage.save();
          return res.status(200).json(savedMessage);
      } catch (err) {
          return res.status(500).json(err);
      }
    }
    else{
      return res.status(400).json({
        "err": true,
        "message": "Users either not matched or blocked"
      }
      );
    }
}

const getMessages = async(req: Request, res: Response, next: NextFunction) =>{
    try {
        const messages = await Message.find({
          conversationId: req.params.conversationId,
        });

        let conversation = await Conversation.findById(req.params.conversationId).catch((err:any)=>{
          return res.status(500).json(err);
        })
        if(!conversation){
          return res.status(404).json({
            "err": true,
            "message": "No Conversation found"
            }
          );
        }
        if(!conversation.matched) return res.status(400).json({"err": true,"message": "Users Not Matched"});
        return res.status(200).json({...messages, blocked: conversation.blocked});
      } catch (err) {
        return res.status(500).json(err);
      }
}

const getLastMessage = async(req: Request, res: Response, next: NextFunction) =>{
    try {
        let conversation = await Conversation.findById(req.params.conversationId).catch((err:any)=>{
          return res.status(500).json(err);
        })
        if(!conversation){
          return res.status(404).json({
            "err": true,
            "message": "No Conversation found"
            }
          );
        }
        if(conversation.blocked){
          return res.status(200).json({text:"Conversation Blocked"});
        }

        const message = await Message.findOne({
          conversationId: req.params.conversationId,
        }).sort({_id:-1});
        if(!message){
          return res.status(404).send({
            "message":"No messages found"
          })
        }
        return res.status(200).json(message);
      } catch (err) {
        return res.status(500).json(err);
      }
}

export default {saveMessage, getMessages, getLastMessage};