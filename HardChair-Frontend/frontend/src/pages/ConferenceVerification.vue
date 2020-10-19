<template>
  <div id="base_verification" v-title data-title="ArkChair - Conference Verification">
    <navbar></navbar>

    <section class="bg-primary header-inner p-0 jarallax position-relative o-hidden" data-overlay>
      <div class="container py-0 layer-2">
        <div class="row my-4 my-md-6 text-light">
          <div class="col-lg-9 col-xl-6">
            <h1 class="display-4">Conference Verification</h1>

          </div>
        </div>
      </div>
    </section>

    
    <section>
      <div class="container">
        <div class="row">
          <div class="col-xl-8 col-lg-12">
            <div class="text item">
              <div v-if = "noMeeting"><el-card shadow="hover">No conference application now!</el-card></div>
              <el-card v-else
                shadow="hover"
                class="box-card"
                style="margin-top: 1em;"
                v-for="conference in conferences.slice((currentPage- 1)*pageSize,currentPage*pageSize)"
                :key="conference.id"
              >
                <div slot="header" class="clearfix">
                  <span style="font-weight: bold">{{conference.nameAbbreviation}}</span>

                  <el-button
                    style="float: right; padding: 3px 0"
                    type="text"
                    @click="verify(conference,'false')"
                  >Reject</el-button>
                  <span style="float: right; padding: 3px 0">&nbsp;&nbsp;</span>
                  <el-button
                    style="float: right; padding: 3px 0"
                    type="text"
                    @click="verify(conference,'true')"
                  >Pass</el-button>
                  <span style="float: right; padding: 3px 0">&nbsp;&nbsp;</span>
                </div>
                <div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-user-solid"></em> Application by:
                    </span>
                    {{conference.owner}}
                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-chat-dot-round"></em> Short name:
                    </span>
                    {{conference.nameAbbreviation}}
                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-chat-line-round"></em> Full name:
                    </span>
                    {{conference.fullName}}
                  </div>
                  <div>
                  <span class="itemlabel">
                  <em class="el-icon-price-tag"></em> Topics:
                  </span>
                  <el-tag
                  :key="index"
                  v-for="(topic,index) in conference.topics.split(',')"
                  >
                  {{topic}}
                  </el-tag>
                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-location"></em> Location:
                    </span>
                    {{conference.location}}
                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-video-play"></em> Starts at:
                    </span>
                    <span v-if = "conference.startTime">
                      {{conference.startTime.substring(0,10)}}
                    </span>
                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-video-pause"></em> Ends at:
                    </span>
                    <span v-if = "conference.endTime">
                      {{conference.endTime.substring(0,10)}}
                    </span>
                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-date"></em> Submission deadline:
                    </span>
                    <span v-if = "conference.deadline">
                      {{conference.deadline.substring(0,10)}}
                    </span>
                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-medal-1"></em> Result announcement at:
                    </span>
                    <span v-if = "conference.resultAnnounceDate">
                      {{conference.resultAnnounceDate.substring(0,10)}}
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
            :page-size = "pageSize" 
            @current-change="pageChange" 
            :current-page.sync="currentPage"
            :total="conferences.length"> 
           >
           </el-pagination>

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
  name: "ConferenceVerification",
  components: { navbar, footerbar },
  inject: ["reload"],
  data() {
    return{
      conferences:[],
      pageSize:6,
      currentPage:1,
      noMeeting: false
    }
  },
  methods: {
    pageChange() {
      this.currentPage = currentPage;
    },
    // Verify the conference
    verify(conference,isAllowed){
      this.$axios.post('/Verify',{
        id:conference.id,
        isAllowed:isAllowed
      })
      .then(resp =>{
        if(resp.status === 200){
          //审核成功刷新页面
          this.reload(); 
        }
      })
      .catch(error=>{
        console.log(error);
      })
    }
  },
  created(){
    // Apply for all unchecked conference information
    this.$axios
    .get('/Verification',{})
    .then(resp => {
      if (resp.status === 200) {
        if(resp.data.length == 0){
          this.noMeeting = true;
        }else{
        this.conferences = resp.data;
        }
      } else {
        this.$message.error("Request Error.")
      }
    })
    .catch(error => {
      console.log(error);
      this.$message.error("Request Error.")
    })
  }
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
.el-tag {
    margin-right: 5px;
  }
</style>
