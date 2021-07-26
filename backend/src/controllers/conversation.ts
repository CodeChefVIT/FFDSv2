import {Request, Response, NextFunction} from 'express';
import logging from '../config/logging';
import Conversation from '../models/Conversation';
import mongoose from 'mongoose';

const NAMESPACE = 'Conversation Controller'

const createNewConversation = async(req: Request, res: Response, next: NextFunction) =>{
  logging.info(NAMESPACE,"Creating New Conversation");
    if(req.body.firstUserId===undefined || req.body.secondUserId===undefined){
        return res.status(400).json({
            "err":true,
            "message": "Either or both ids are null"
        });
    }
    const newConversation = new Conversation({
        members: [req.body.firstUserId, req.body.secondUserId],
      });
    
      try {
        const savedConversation = await newConversation.save();
        return res.status(200).json(savedConversation);
      } catch (err) {
        return res.status(500).json(err);
      }
}

const getConversationOfUser = async(req: Request, res: Response, next: NextFunction) =>{
  logging.info(NAMESPACE,"Getting all Conversations of a user");
  let userId = res.locals.jwt.id;
  try {
    const conversation = await Conversation.find({
      members: { $in: [userId] },
    });
    return res.status(200).json(conversation);
  } catch (err) {
    return res.status(500).json(err);
  }
}

const getPairConversation = async(req: Request, res: Response, next:NextFunction) =>{
  logging.info(NAMESPACE,"Getting particular conversation of a user");
  let selfUserId = res.locals.jwt.id;
  console.log(selfUserId,req.params.userId)
  try {
    const conversation = await Conversation.findOne({
      members: { $all: [selfUserId, req.params.userId] },
    });
    if(!conversation) return res.status(404).send({message:"No Conversation found"})
    return res.status(200).json(conversation)
  } catch (err) {
    return res.status(500).json(err);
  }
}

export default {createNewConversation,getConversationOfUser,getPairConversation}