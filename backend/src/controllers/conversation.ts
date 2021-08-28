import {Request, Response, NextFunction} from 'express';
import logging from '../config/logging';
import Conversation from '../models/Conversation';
import User from '../models/User';

const NAMESPACE = 'Conversation Controller'

const createNewConversation = async(req: Request, res: Response, next: NextFunction) =>{
  logging.info(NAMESPACE,"Creating New Conversation");
  let selfUserId = res.locals.jwt.id;
    if(req.body.userId===undefined){
        return res.status(400).json({
            "err":true,
            "message": "User id is null"
        });
    }

    const selfUser = await User.findById(selfUserId).catch((err:any)=>{
      return res.status(500).json(err);
    })
    
    const secondUser = await User.findById(selfUserId).catch((err:any)=>{
      return res.status(500).json(err);
    })

    if(selfUser===null || selfUser===undefined || secondUser===null || selfUser===undefined){
      return res.status(404).json({
        "err":true,
        "message": "User not found"
    });
    }

    selfUser.accepted.push(req.body.userId);
    await selfUser.save();

    const conversation = await Conversation.findOne({
      members: { $all: [selfUserId, req.params.userId] },
    }).catch((err:any)=>{
      return res.status(500).json(err);
    });

    if(conversation !== null && conversation !== undefined){
      conversation.matched = true;
      conversation.save();
      return res.status(200).json({
        ...conversation,
        message: "Users Matched Successfully"
      }
      );
    }

    const newConversation = new Conversation({
        members: [selfUserId, req.body.userId],
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
  let matchedOnly = [];
  let hasMessages = [];
  try {
    const conversations = await Conversation.find({
      "members": { $in: [userId] }, "matched": true
    });
    matchedOnly = conversations.filter((conversation:any) => !conversation.hasMessages && !conversation.blocked);
    hasMessages = conversations.filter((conversation:any) => conversation.hasMessages);
    return res.status(200).json({
      matchedOnly,
      hasMessages
    });
  } catch (err) {
    return res.status(500).json(err);
  }
}

const unBlock = async(req: Request, res: Response, next:NextFunction) =>{
  logging.info(NAMESPACE,"Unblocking a user");
  let selfUserId = res.locals.jwt.id;
  try {
    const conversation = await Conversation.findOne({
      "members": { $all: [selfUserId, req.params.userId] },
    });
    if(conversation===null || conversation===undefined){
      return res.status(404).send({
        "err":true
      })
    }
    const selfUser = await User.findById(selfUserId);
    if(!conversation.blocked) return res.status(400).send({message:"User not blocked"})
    else{
      if(selfUser.blocked.includes(req.params.userId)){
        conversation.blocked = false;
        conversation.save();
        const index = selfUser.blocked.indexOf(req.params.userId);
        selfUser.blocked.splice(index, 1);
        selfUser.save();
        return res.status(200).send(conversation)
      }else{
        return res.status(405).send({
          message:"This user is not allowed to unblock"
        })
      }
    }
  } catch (err) {
    return res.status(500).json(err);
  }
}

const block = async(req: Request, res: Response, next:NextFunction) =>{
  logging.info(NAMESPACE,"Blocking a user");
  let selfUserId = res.locals.jwt.id;
  try {
    const conversation = await Conversation.findOne({
      "members": { $all: [selfUserId, req.params.userId] },
    });
    const selfUser = await User.findById(selfUserId);
    if(conversation===null || conversation===undefined){
      return res.status(404).send({
        "err":true,
        "message":"Conversation not found"
      })
    }
    if(!conversation.matched) return res.status(400).send({err:true, message:"Users not Matched"})
    if(selfUser===null || selfUser===undefined){
      return res.status(404).send({
        "err":true,
        "message":"User not found"
      })
    }
    if(conversation.blocked && selfUser.blocked.includes(req.params.userId)) return res.status(400).send({message:"User already blocked"})
    else{
      conversation.blocked = true;
      selfUser.blocked.push(req.params.userId);
      conversation.save();
      selfUser.save();
    }
  } catch (err) {
    return res.status(500).json(err);
  }
}

const getPairConversation = async(req: Request, res: Response, next:NextFunction) =>{
  logging.info(NAMESPACE,"Getting particular conversation of a user");
  let selfUserId = res.locals.jwt.id;
  try {
    const conversation = await Conversation.findOne({
      "members": { $all: [selfUserId, req.params.userId] },
    });
    if(!conversation.matched) return res.status(400).send({message:"Users Not Matched"})
    if(!conversation) return res.status(404).send({message:"No Conversation found"})
    return res.status(200).json(conversation)
  } catch (err) {
    return res.status(500).json(err);
  }
}


export default {createNewConversation,getConversationOfUser,getPairConversation,block,unBlock}