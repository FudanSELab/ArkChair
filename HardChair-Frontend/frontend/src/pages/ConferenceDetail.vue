<template>
  <div id="base_verification" v-title data-title="ArkChair - Conference Detail">
    <navbar></navbar>

    <section class="bg-primary header-inner p-0 jarallax position-relative o-hidden" data-overlay>
      <div class="container py-0 layer-2">
        <div class="row my-4 my-md-6 text-light">
          <div class="col-lg-9 col-xl-6">
            <h1 class="display-4">{{conference.nameAbbreviation}}</h1>
            <p class="lead mb-0">{{conference.fullName}}</p>
            <br />
            <el-button
              class="lead mb-0"
              v-if="seeChangeAuthority"
              @click="seeChooseAuthority = true"
            >Change Authority</el-button>
          </div>
        </div>
      </div>
    </section>

    <div class="contentContainer">
      <div class="container">
        <el-tabs v-model="activeName">
          <el-tab-pane label="Conference Info" name="info">
            <section>
              <div class="row">
                <div class="col-xl-8 col-lg-8">
                  <div>
                    <h2>
                      <em class="el-icon-info"></em> Conference Info
                    </h2>
                    <div class="infoitem">
                      <span class="itemlabel">
                        <em class="el-icon-user-solid"></em> Owner:
                      </span>
                      {{conference.owner}}
                    </div>
                    <div class="infoitem">
                      <span class="itemlabel">
                        <em class="el-icon-chat-dot-round"></em> Short name:
                      </span>
                      {{conference.nameAbbreviation}}
                    </div>
                    <div class="infoitem">
                      <span class="itemlabel">
                        <em class="el-icon-chat-line-round"></em> Full name:
                      </span>
                      {{conference.fullName}}
                    </div>
                    <div class="infoitem" v-if="conference.topics">
                      <span class="itemlabel">
                        <em class="el-icon-price-tag"></em> Topics:
                      </span>
                      <el-tag
                        :key="index"
                        v-for="(topic,index) in conference.topics.split(',')"
                      >{{topic}}</el-tag>
                    </div>
                    <div class="infoitem">
                      <span class="itemlabel">
                        <em class="el-icon-location"></em> Location:
                      </span>
                      {{conference.location}}
                    </div>
                    <div class="infoitem">
                      <span class="itemlabel">
                        <em class="el-icon-video-play"></em> Starts at:
                      </span>
                      <span v-if="conference.startTime">{{conference.startTime.substring(0, 10) }}</span>
                    </div>
                    <div class="infoitem">
                      <span class="itemlabel">
                        <em class="el-icon-video-pause"></em> Ends at:
                      </span>
                      <span v-if="conference.endTime">{{conference.endTime.substring(0, 10)}}</span>
                    </div>
                    <div class="infoitem">
                      <span class="itemlabel">
                        <em class="el-icon-date"></em> Submission deadline:
                      </span>
                      <span v-if="conference.deadline">{{conference.deadline.substring(0, 10)}}</span>
                    </div>
                    <div class="infoitem">
                      <span class="itemlabel">
                        <em class="el-icon-medal-1"></em> Result announcement at:
                      </span>
                      <span
                        v-if="conference.resultAnnounceDate"
                      >{{conference.resultAnnounceDate.substring(0,10)}}</span>
                    </div>
                    <div class="infoitem">
                      <span class="itemlabel">
                        <em class="el-icon-s-flag"></em> Status:
                      </span>
                      <conferenceStatus :status="conference.status"></conferenceStatus>
                    </div>
                  </div>
                </div>
              </div>
            </section>
          </el-tab-pane>

          <el-tab-pane v-if="isCHAIR" label="Conference Operations" name="operation">
            <section>
              <div class="row">
                <div class="col-xl-8 col-lg-8">
                  <h2>
                    <em class="el-icon-magic-stick"></em> Conference Operations
                  </h2>

                  <div class="row">
                    <div v-if="isCHECKED">
                      <el-button
                        class="onPageBtn"
                        type="primary"
                        @click="startContribution()"
                      >Start accepting papers</el-button>
                    </div>

                    <div v-if="isCHECKED || isSUBMIT_ALLOWED ">
                      <el-button
                        class="onPageBtn"
                        type="primary"
                        @click="dialogFormVisible = true"
                      >Invite PC member</el-button>
                    </div>

                    <div
                      v-if="isCHECKED || isSUBMIT_ALLOWED ||isOPEN_REVIEW || isOPEN_RESULT ||isOPEN_FINAL_RESULT||isFINISHED"
                    >
                      <el-button
                        class="onPageBtn"
                        type="primary"
                        @click="updateInvitation()"
                      >See current PC members</el-button>
                    </div>

                    <div v-if="isSUBMIT_ALLOWED">
                      <el-button
                        class="onPageBtn"
                        type="primary"
                        @click="seeChooseStrategy = true"
                      >Start review</el-button>
                    </div>

                    <div>
                      <el-button
                        v-if="isOPEN_REVIEW"
                        class="onPageBtn"
                        type="primary"
                        @click="announceResults"
                      >Announce Results</el-button>
                    </div>

                    <div>
                      <el-button
                        v-if="isOPEN_RESULT"
                        class="onPageBtn"
                        type="primary"
                        @click="announceFinalResults"
                      >Announce Final Results</el-button>
                    </div>

                  </div>
                </div>
              </div>
            </section>
          </el-tab-pane>

          <el-tab-pane
            v-if="!isCHAIR && !isADMIN && isSUBMIT_ALLOWED && !forbidContribute"
            label="Paper Submission"
            name="contribution"
          >
            <section>
              <div class="row">
                <div class="col-xl-6 col-lg-6">
                  <h2>
                    <em class="el-icon-upload2"></em> Paper Submission
                  </h2>
                  <contribution
                    v-if="this.conference.id"
                    :conferenceId="conference.id"
                    :topics="conference.topics"
                  ></contribution>
                </div>
              </div>
            </section>
          </el-tab-pane>

          <el-tab-pane v-if=" isAUTHOR" label="My Papers" name="myPaper">
            <section>
              <div class="row">
                <div class="col-xl-6 col-lg-6">
                  <h2>
                    <em class="el-icon-document"></em>My Papers
                  </h2>
                  <showPapers :paper="papers" key="1"></showPapers>                                    
                </div>
              </div>
            </section>
          </el-tab-pane>
          
          <el-tab-pane v-if="isPC_MEMBER" label="Papers to review" name="review">
            <section>
              <div class="row">
                <div class="col-xl-6 col-lg-6">
                  <h2>
                    <em class="el-icon-document"></em> Papers to review
                  </h2>
                  <showPapers v-if="conference.id" :conferenceId="conference.id" key="2"></showPapers>
                </div>
              </div>
            </section>
          </el-tab-pane>

          <el-tab-pane v-if="isCHAIR" label="All Papers" name="allPapers">
            <section>
              <div class="row">
                <div class="col-xl-6 col-lg-6">
                  <h2>
                    <em class="el-icon-document"></em> All papers contributed
                  </h2>
                  <showPapers :paper="papers" key="3"></showPapers>                  
                </div>
              </div>
            </section>
          </el-tab-pane>
        </el-tabs>
      </div>
    </div>

    <!-- Invite dialog -->
    <el-dialog title="Invite PC members to your conference" :visible.sync="dialogFormVisible">
      <el-form
        @submit.native.prevent
        status-icon
        :model="inviteForm"
        :rules="inviteRules"
        :inline="true"
        label-position="left"
        v-loading="loading"
        :ref="inviteForm"
        hide-required-asterisk
      >
        <!-- user full name-->
        <el-form-item prop="fullName" label="Search user by their real names:">
          <el-input
            type="text"
            v-model="inviteForm.fullName"
            auto-complete="off"
            id="fullName"
            placeholder="Real name of the user"
          ></el-input>
        </el-form-item>

        <!-- submit button -->
        <el-form-item>
          <el-button
            native-type="submit"
            :disabled="isSearchDisabled"
            type="primary"
            style="width: 100%"
            v-on:click="search(inviteForm)"
          >Search</el-button>
        </el-form-item>
        <el-form-item>
          <el-button v-if="searched" @click="invite()" type="primary">Invite</el-button>
        </el-form-item>
      </el-form>

      <!-- display search result -->
      <el-table
        @selection-change="handleSelectionChange"
        :data="users"
        style="width: 100%"
        max-height="250"
      >
        <el-table-column type="selection" width="50"></el-table-column>
        <el-table-column fixed prop="fullname" label="Real name" width="150"></el-table-column>
        <el-table-column prop="email" label="E-mail" width="150"></el-table-column>
        <el-table-column prop="region" label="Region" width="150"></el-table-column>
        <el-table-column prop="organization" label="Organization" width="150"></el-table-column>
      </el-table>
    </el-dialog>

    <!-- Look for current pc_members -->
    <el-dialog title="See current PC member" :visible.sync="dialogMemberTableVisible">
      <!-- pc_members display table -->
      <el-table
        @selection-change="handleSelectionChange"
        :data="pcMembers"
        style="width: 100%"
        max-height="250"
      >
        <el-table-column fixed prop="fullname" label="Real name" width="130"></el-table-column>
        <el-table-column prop="email" label="E-mail" width="130"></el-table-column>
        <el-table-column prop="region" label="Region" width="130"></el-table-column>
        <el-table-column prop="organization" label="Organization" width="130"></el-table-column>
        <el-table-column
          prop="password"
          label="Status"
          width="130"
          :filters="[{ text: 'UNREAD', value: 'UNREAD' },{ text: 'ACCEPT', value: 'ACCEPT' }, { text: 'REJECT', value: 'REJECT' }]"
          :filter-method="filterTag"
          filter-placement="bottom-end"
        >
          <template slot-scope="scope">
            <el-tag
              :type="handleType(scope.row.password)"
              disable-transitions
            >{{scope.row.password}}</el-tag>
          </template>
        </el-table-column>
      </el-table>
    </el-dialog>

    <!-- Choose Authority -->
    <el-dialog
      title="Choose Authority"
      :visible.sync="seeChooseAuthority"
      width="50%"
      :show-close="false"
      :close-on-click-modal="false"
      :close-on-press-escape="false"
    >
      <p>You have more than 1 authority in this conference, please choose one to enter</p>
      <el-radio-group v-model="chooseAuthority" @change="changeAuthority">
        <el-radio
          class="checkboxes"
          border
          v-for="authority in authorities"
          :key="authority.id"
          :label="authority.authority"
        >{{authority.authority}}</el-radio>
      </el-radio-group>
      <span slot="footer" class="dialog-footer">
        <el-button type="primary" @click="seeChooseAuthority = false" :disabled="hasChosen">Enter</el-button>
      </span>
    </el-dialog>

    <div>
      <!-- chooose review strategy -->
      <el-dialog
        title="Choose Distribution Strategy"
        :visible.sync="seeChooseStrategy"
        width="50%"
        :show-close="true"
        :close-on-click-modal="true"
        :close-on-press-escape="true"
      >
        <p>Please choose a strategy to distribute papers contributed to this conference.</p>
        <p>
          <el-radio v-model="strategy" label="1" border>By topic correlation</el-radio>
        </p>
        <p>
          <el-radio v-model="strategy" label="2" border>By even distribution</el-radio>
        </p>
        <span slot="footer" class="dialog-footer">
          <el-button type="primary" @click="startReview()">Confirm</el-button>
        </span>
      </el-dialog>
    </div>

    <footerbar></footerbar>
  </div>
</template>

<script>
import navbar from "../components/Nav";
import footerbar from "../components/Footer";
import draggable from "vuedraggable";
import download from "../components/DownloadPaper";
import preview from "../components/PreviewPaper";
import contribution from "../components/SubmitPaper";
import showPapers from "../components/ShowReviewPapers";
import conferenceStatus from "../components/ParseConferenceStatus";

export default {
  name: "ConferenceDetail",
  components: {
    navbar,
    footerbar,
    draggable,
    download,
    preview,
    contribution,
    showPapers,
    conferenceStatus
  },
  inject: ["reload"],

  data() {
    return {
      // Conference Information
      papers: [],
      authorities: [],
      conference: {},
      pcMembers: [],
      isCHECKED: false,
      isSUBMIT_ALLOWED: false,
      isFINISHED: false,
      isOPEN_REVIEW: false,
      isOPEN_RESULT: false,
      isOPEN_FINAL_RESULT:false,

      // Visitor authority
      isADMIN: false,
      isCHAIR: false,
      isPC_MEMBER: false,
      isAUTHOR: false,
      forbidContribute: false,

      // Choose Authority
      hasChosen: true,
      chooseAuthority: "",
      seeChooseAuthority: false,
      seeChangeAuthority: false,
      isSearchDisabled: true,

      // Paper paging
      pageSize: 2,
      currentPage: 1,

      // show Pc_member dialog
      dialogMemberTableVisible: false,

      // start review
      seeChooseStrategy: false,
      strategy: "",

      // tab page
      activeName: "info",

      loading: false,

      // Search & invite form
      dialogFormVisible: false,
      inviteForm: {
        fullName: ""
      },
      inviteRules: {
        fullName: [
          {
            required: true,
            message: "Please enter the real name",
            trigger: "blur"
          },
          {
            validator: (rule, value, callback) => {
              this.isSearchDisabled = this.inviteForm.fullName == "";
              this.users = [];
              callback();
            },
            trigger: "change"
          }
        ]
      },
      users: [],
      searched: false,
      multipleSelection: [],
      inviteUsers: []
    };
  },
  methods: {
    // 1. Change visitor authority
    changeAuthority(val) {
      switch (val) {
        case "CHAIR":
          this.isCHAIR = true;
          this.isPC_MEMBER = false;
          this.isAUTHOR = false;
          this.activeName = "operation";
          break;
        case "PC_MEMBER":
          this.isPC_MEMBER = true;
          this.isCHAIR = false;
          this.isAUTHOR = false;
          this.activeName = "review";
          break;
        case "AUTHOR":
          this.isAUTHOR = true;
          this.isCHAIR = false;
          this.isPC_MEMBER = false;
          this.activeName = "myPaper";
          break;
      }
      this.hasChosen = false;
    },

    // 2. Start contribution
    startContribution() {
      this.$axios
        .post("/ConferenceOpenSubmit", {
          conferenceId: this.conference.id
        })
        .then(resp => {
          if (resp.status === 200) {
            this.isCHECKED = false;
            this.isSUBMIT_ALLOWED = true;
            this.conference.status = "SUBMIT_ALLOWED";
            this.$message({
              dangerouslyUseHTMLString: true,
              type: "success",
              message:
                '<strong style="color:teal">Your conference has started to accept papers!</strong>',
              center: true
            });
          } else {
            this.$message("Request Error!");
          }
        })
        .catch(error => {
          this.$message("Request Error!");
          console.log(error);
        });
    },

    // 3. Search & Invite PC_MEMBER
    search(formName) {
      //In case of some bug, still validate before submit
      this.$refs[formName].validate(valid => {
        if (valid) {
          this.$axios
            .post("/SearchByFullName", {
              fullname: this.inviteForm.fullName,
              conferenceId: this.conference.id
            })
            .then(resp => {
              if (resp.status === 200) {
                if (resp.data.length != 0) {
                  this.users = resp.data;
                  this.searched = true;
                } else {
                  this.$message("No result!");
                }
              } else {
                this.$message.error("Request Error!");
              }
            })
            .catch(error => {
              console.log(error);
              this.$message.error("Request Error!");
            });
        } else {
          this.$message.error("Wrong submit! Please check the form.");
        }
      });
    },
    handleSelectionChange(val) {
      this.multipleSelection = val;
    },
    invite() {
      let len = this.multipleSelection.length;
      if (len > 0) {
        for (let i = 0; i < len; i++) {
          this.inviteUsers.push(this.multipleSelection[i].id);
        }
        this.$axios
          .post("/DistributeAuthority", {
            conferenceId: this.conference.id,
            users: this.inviteUsers
          })
          .then(resp => {
            if (resp.status === 200) {
              this.searched = false;
              this.inviteForm.fullName = "";
              this.users = [];
              this.$message({
                dangerouslyUseHTMLString: true,
                type: "success",
                message:
                  '<strong style="color:teal">Invitation has been sent!</strong>',
                center: true
              });
            } else {
              this.$message("Request Error!");
            }
          })
          .catch(error => {
            this.$message("Request Error!");
            console.log(error);
          });
      } else {
        this.$message("Please choose at least one user !");
      }
    },

    // 4. See PC_MEMBER
    updateInvitation() {
      this.$axios
        .post("/FindInvitationStatus", {
          conferenceId: this.conference.id
        })
        .then(resp => {
          if (resp.status === 200) {
            this.pcMembers = resp.data;
          } else {
            this.$message.error("Request Error");
          }
        })
        .catch(error => {
          this.$message.error("Request Error");
          console.log(error);
        });
      this.dialogMemberTableVisible = true;
    },
    // Invitation Status Tag
    handleType(tag) {
      switch (tag) {
        case "UNREAD":
          return "info";
          break;
        case "ACCEPT":
          return "success";
          break;
        case "REJECT":
          return "danger";
      }
    },
    filterTag(value, row) {
      return row.password === value;
    },

    // 5. Start Review
    startReview() {
      this.seeChooseStrategy = true;
      if (this.strategy !== "") {
        this.$axios
          .post("/OpenReview", {
            conferenceId: this.conference.id,
            strategy: Number(this.strategy)
          })
          .then(resp => {
            if (resp.status === 200 ) {
              switch(resp.data.message1){
                case "open success":
                  this.isSUBMIT_ALLOWED = false;
                  this.isOPEN_REVIEW = true;
                  this.conference.status = "OPEN_REVIEW";
                  this.notify("PC members will start to review papers!","success");
                  this.seeChooseStrategy = false;
                  break;
                case "open fail: at least 1 paper is expected":
                  this.notify("Fail since no paper contributed to the conference.","error");
                  break;
                case "open fail: number of PC MEMBER should more than 2":
                  this.notify("Fail since less than 2 pc members in the conference.","error");                  
                  break;
                case "open fail : no solution":
                  this.notify("Fail since paper distribution error.","error");
                  break;
              }                                
            }
          })
          .catch(error => {
            console.log(error);
          });
      } else {
        this.notify("Please choose a strategy!","warning");
      }
      this.strategy = "";
    },
    announceResults() {
      this.$axios
        .post("/OpenResult", {
          conferenceId: this.conference.id
        })
        .then(resp => {
          if(resp.status === 200){
            switch(resp.data.message){
              case "open success":
                this.isOPEN_REVIEW = false;
                this.isOPEN_RESULT = true;
                this.conference.status = "OPEN_RESULT";                                
                this.notify("Open success!","success");
                break;
              case "open fail: wait for review":
                this.notify("There are papers waiting to review now!","error");
                break;
              case "wait for all review results to be confirmed or revised!":
                this.notify("There are review results to be confirmed or revised!","error");
                break;
            }
          }
        })
        .catch(error => {
          console.log(error);
        });
    },
    announceFinalResults(){
      this.$axios.post('/OpenFinalResult',{
        conferenceId:this.conference.id
      })
      .then(resp=>{
        if(resp.status === 200){
          switch(resp.data.message){
            case "wait for all review results to be confirmed or revised!":
              this.notify("Wait for all review results to be confirmed or revised!","error");
              break;
            case "success":
              this.isOPEN_RESULT = false;
              this.isOPEN_FINAL_RESULT = true;
              this.conference.status = "OPEN_FINAL_RESULT";                
              this.notify("Open success!","success");
              break;
          }
        }
      })
      .catch(error=>{console.log(error)});      
    },
    notify(content,format){
      this.$message({
        dangerouslyUseHTMLString: true,
        type: format,
        message:'<strong style="color:teal">'+content+'</strong>',
        center: true
      });      
    }
  },
  created() {
    //获取会议信息
    this.$axios
      .post("/ConferenceDetails", {
        id: this.$route.params.conferenceID
      })
      .then(resp => {
        if (resp.status === 200) {
          this.papers = resp.data.papers.reverse();
          this.authorities = resp.data.authorities;
          this.conference = resp.data.conference;
          // Authority
          //Don't display function part for ADMIN
          if (this.$store.state.userType == "ADMIN") {
            this.isADMIN = true;
          } else {
            // Normal user
            let len = this.authorities.length;
            for (let i = 0; i < len; i++) {
              switch (this.authorities[i].authority) {
                case "CHAIR":
                  this.isCHAIR = true;
                  this.forbidContribute = true;
                  break;
                case "PC_MEMBER":
                  this.isPC_MEMBER = true;
                  break;
                case "AUTHOR":
                  this.isAUTHOR = true;
                  break;
              }
            }
          }

          // Conference Status
          switch (this.conference.status) {
            case "CHECKED":
              this.isCHECKED = true;
              break;
            case "SUBMIT_ALLOWED":
              this.isSUBMIT_ALLOWED = true;
              break;
            case "FINISHED":
              this.isFINISHED = true;
              break;
            case "OPEN_REVIEW":
              this.isOPEN_REVIEW = true;
              break;
            case "OPEN_RESULT":
              this.isOPEN_RESULT = true;
              break;
            case "OPEN_FINAL_RESULT":
              this.isOPEN_FINAL_RESULT = true;
              break;
          }

          if (this.authorities.length > 1) {
            this.seeChooseAuthority = true;
            this.seeChangeAuthority = true;
          }
        } else {
          this.$message("Request Error");
        }
      })
      .catch(error => {
        console.log(error);
        this.$message("Request Error");
      });
  }
};
</script>

<style scoped>
.contentContainer {
  padding: 2em;
}
section {
  padding: 0.5em;
}
.itemlabel,
h2 {
  color: #3755be;
  font-weight: bold;
}
.infoitem {
  margin-top: 1em;
  margin-bottom: 1em;
}
.onPageBtn {
  margin-left: 12px;
}
.el-tag {
  margin-right: 5px;
}
.input-new-tag {
  width: 103px;
  margin-right: 5px;
  vertical-align: bottom;
}
.checkboxes {
  margin-right: 0;
}
</style>
