import mongoose, { Schema } from 'mongoose';

const ConversationSchema: Schema = new Schema(
  {
    members: {
      type: Array,
      required:true
    },
  },
  { timestamps: true }
);

var conversationModel = mongoose.model("Conversation", ConversationSchema);

export default conversationModel;