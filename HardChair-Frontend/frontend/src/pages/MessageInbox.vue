<template>
  <div id="base_verification" v-title data-title="ArkChair - Message Inbox">
    <navbar></navbar>

    <section class="bg-primary header-inner p-0 jarallax position-relative o-hidden" data-overlay>
      <div class="container py-0 layer-2">
        <div class="row my-4 my-md-6 text-light">
          <div class="col-lg-9 col-xl-6">
            <h1 class="display-4">Message Inbox</h1>
          </div>
        </div>
      </div>
    </section>

    <section>
      <div class="container">
        <div class="row">
          <div class="col-xl-8 col-lg-12">
            <div class="text item">
              <div v-if="noMessage">
                <el-card shadow="hover">No message now!</el-card>
              </div>
              <el-card
                v-else
                shadow="hover"
                class="box-card"
                style="margin-top: 1em;"
                v-for="(message,index) in messages.slice((currentPage- 1)*pageSize,currentPage*pageSize)"
                :key="index"
              >
              <!-- header -->
                <div slot="header" class="clearfix">
                  <span v-if="message.status" >{{parseMessageType(message.type)}}</span>
                  <span v-else>
                    <el-badge is-dot class="item"></el-badge><span style="font-weight: bold"> {{parseMessageType(message.type)}}</span>
                  </span>
                  
                   <!--PC_MEMBER_INVITATION-->
                  <span v-if = "message.type == 'PC_MEMBER_INVITATION' && !message.status">
                    <el-button
                      style="float: right; padding: 3px 0"
                      type="text"
                      @click="response(message.content,message.messageId,'reject',index)"
                    >Reject</el-button>

                    <span style="float: right; padding: 3px 0">&nbsp;&nbsp;</span>                    
                    <el-button
                      style="float: right; padding: 3px 0"
                      type="text"
                      @click="response(message.content,message.messageId,'accept',index)"
                    >Agree</el-button>                                         
                  </span>
                  
                  <!--CONFERENCE_CHECKED CONFERENCE_ABOLISHED PC_MEMBER_ACCEPTED PC_MEMBER_REJECTED -->
                  <el-button
                    v-else-if = "!message.status"
                    style="float: right; padding: 3px 0"
                    type="text"
                    @click="mark(message.messageId)"
                  >Mark as read</el-button>

                  <span style="float: right; padding: 3px 0">&nbsp;&nbsp;</span>
                </div>

                <div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-chat-line-round"></em> Content:
                    </span>
                    <span v-if="message.content">
                      {{parseMessageContent(message.type, message.content)}}
                    </span>
                  </div>

                  <div v-if = "message.tag && !message.status">
                    <span class="itemlabel">
                      Topic:
                    </span>
                    <span>
                      <el-checkbox-group v-model="chosenTopics[index]">
                      <el-checkbox
                      v-for = "topic in message.tag.split(',')"
                      :key = "topic"
                      :label="topic"
                      >
                      </el-checkbox>
                      </el-checkbox-group>
                    </span>

                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-time"></em> Sent Time:
                    </span>
                    <span v-if="message.sentTime">
                    {{message.sentTime.substring(0,10)}}
                    </span>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
        </div>
        <br />

        <div class="row">
          <div class="col-xl-6 col-lg-12">
            <el-pagination
              hide-on-single-page
              layout="prev, pager, next"
              :page-size="pageSize"
              :current-page.sync="currentPage"
              :total="messages.length"
            >></el-pagination>
          </div>
        </div>
      </div>
    </section>
    
    <footerbar></footerbar>
  </div>
</template>

<script>
import navbar from "../components/Nav";
import footerbar from "../components/Footer";

export default {
  name: "MessageInbox",
  components: { navbar, footerbar },
  inject: ["reload"],
  data() {
    return {
      messages: [],
      pageSize: 6,
      currentPage: 1,
      noMessage: false,
      chosenTopics:[[]],
    };
  },
  methods: {
    parseMessageType(messageType){
      switch (messageType) {
        case "PC_MEMBER_INVITATION":
          return "You are invited to be a PC member"
          break;
        case "CONFERENCE_CHECKED":
          return "The conference you applied has been approved"
          break;
        case "CONFERENCE_ABOLISHED":
          return "The conference you applied has been rejected"
          break;
        case "PC_MEMBER_ACCEPTED":
          return "Your PC member invitation has been accepted"
          break;
        case "PC_MEMBER_REJECTED":
          return "Your PC member invitation has been rejected"
          break;
        default:
          return "N/A"
          break;
      }
    },
    parseMessageContent(messageType, messageContent){
      let contents = messageContent.split(",");
      switch (messageType) {
        case "PC_MEMBER_INVITATION":
          return contents[0] + " invited you to be the PC MEMBER of the conference "+ contents[1] +".";
          break;
        case "CONFERENCE_CHECKED":
          return "The conference " + contents[0] + " which you applied have been approved by administrator.";
          break;
        case "CONFERENCE_ABOLISHED":
          return "The conference " + contents[0] + " which you applied have been rejected by administrator.";
          break;
        case "PC_MEMBER_ACCEPTED":
          return contents[0] +" have accepted to be the PC MEMBER of your conference " + contents[1] + ".";
          break;
        case "PC_MEMBER_REJECTED":
          return contents[0] +" have rejected to be the PC MEMBER of your conference " + contents[1] + ".";
          break;
        default:
          return "N/A. Please contact the admin for details.";
          break;
      }
    },

    /**  Message Operation **/
    // Mark as read
    mark(id){
      this.$axios.post('/MessageAlreadyRead',{
        messageId : id
      })
      .then(resp=>{
        if ( resp.status === 200){
          this.reload();
        }else{
          this.$message.error("Request Error!");
        }
      })
      .catch(error =>{
        this.$message.error("Request Error!");
        console.log(error);
      })
    },
    // Agree the invitation
    response(content,messageId,opinion,index) {
      if(opinion == "accept" && this.chosenTopics[index].length == 0){
        this.$message({
          dangerouslyUseHTMLString: true,
          type:'warning',
          message: '<strong style="color:teal">Please choose at least one topic.</strong>',
          center:true
          });
        return;  
      }

      if(opinion == 'reject'){
        this.chosenTopics[index]=[];
      }

      let start = content.lastIndexOf(',')+1;
      let conferenceId = content.substring(start);
      this.$axios.post('/AuthorityAcceptedOrRejected',{
         conferenceId:conferenceId,
         acceptOrRejected:opinion,
         messageId:messageId,
         topics:this.chosenTopics[index]
       })
       .then(resp =>{
         if(resp.status === 200){
           this.reload(); 
         }
       })
      .catch(error=>{
         console.log(error);
       })
    }
  },
  created() {
    // Apply for message information
    this.$axios
      .get("/Message", {})
      .then(resp => {
        if (resp.status === 200) {
          if (resp.data.length == 0) {
            this.noMessage = true;
          } else {
            this.messages = resp.data;
            for(var i=0;i<this.messages.length;i++){ 
              this.chosenTopics[i]=[];
            }
          }
        } else {
          this.$message.error("Request Error.");
        }

      })
      .catch(error => {
        console.log(error);
        this.$message.error("Request Error.");
      });
  },

};
</script>

<style scoped>
section {
  padding: 2em;
}
.itemlabel {
  color: #3755be;
  font-weight: bold;
}
</style>
