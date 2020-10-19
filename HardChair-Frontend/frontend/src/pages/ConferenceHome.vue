<template>
  <div id="base_verification" v-title data-title="ArkChair - Conference Home">
    <navbar></navbar>

    <section class="bg-primary header-inner p-0 jarallax position-relative o-hidden" data-overlay>
      <div class="container py-0 layer-2">
        <div class="row my-4 my-md-6 text-light">
          <div class="col-lg-9 col-xl-6">
            <h1 class="display-4">Conference Home</h1>
            <p class="lead mb-0">Check out ongoing conferences here.</p>
            <br/>
            <el-select v-model="value" placeholder="Filter status" @change="statusFilter">
              <el-option
                v-for="item in options"
                :key="item.value"
                :label="item.label"
                :value="item.value"
                >
              </el-option>
            </el-select>
          </div>
        </div>
      </div>
    </section>

    <section>
      <div class="container">
        <div class="row">
          <div class="col-xl-8 col-lg-12">
            <div class="text item">
              <div v-if = "conferences.length == 0"><el-card shadow="hover">No conference now!</el-card></div>
              <el-card 
                v-else
                shadow="hover"
                class="box-card"
                style="margin-top: 1em;"
                v-for="conference in conferences.slice((currentPage- 1)*pageSize,currentPage*pageSize)"
                :key="conference.id"
              >
                <div slot="header" class="clearfix">
                  <span style="font-weight: bold">{{conference.nameAbbreviation}}</span>
                  <router-link :to="'conference-detail/'+conference.id" style="float: right; padding: 3px 0" >View details</router-link>
                </div>
                <div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-chat-line-round"></em> Full name:
                    </span>
                    {{conference.fullName}}
                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-s-flag"></em> Status:
                    </span>
                    <conferenceStatus v-if = "conference.status" :status="conference.status"></conferenceStatus>
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
import conferenceStatus from "../components/ParseConferenceStatus";

export default {
  name: "ConferenceHome",
  components: { navbar, footerbar,conferenceStatus },
  data() {
    return{
      conferences:[],
      all:[],
      checked:[],
      submitAllowed:[],
      openReview:[],
      openResult:[],
      openFinalResult:[],
      pageSize:6,
      currentPage:1,
      

      options: [{
        value:'ALL',
        label: 'All conferences'
      },{
        value: 'CHECKED',
        label: 'Approved by admin'
      }, {
        value: 'SUBMIT_ALLOWED',
        label: 'Accepting papers'
      }, {
        value: 'OPEN_REVIEW',
        label: 'Papers reviewed'
      }, {
        value: 'OPEN_RESULT',
        label: 'Result announced'
      }, {
        value: 'OPEN_FINAL_RESULT',
        label: 'Final Result announced'
      }],
      value: 'ALL'

    }
  },
  methods: {
    pageChange(){
      this.currentPage = currentPage
    },
    statusFilter(value){
      switch(value){
        case "ALL":
          this.conferences = this.all;
          break;
        case "CHECKED":
          this.conferences = this.checked;
          break;
        case "SUBMIT_ALLOWED":
          this.conferences = this.submitAllowed;
          break;
        case "OPEN_REVIEW":
          this.conferences = this.openReview;
          break;
        case "OPEN_RESULT":
          this.conferences = this.openResult;
          break;
        case "OPEN_FINAL_RESULT":
          this.conferences = this.openFinalResult;
          break;
        default:
          this.conferences = this.all;
          break;
      }
    }
  },
  created(){
    // 获取会议列表
    this.$axios
    .get('/ShowConferences',{})
    .then(resp => {
      if (resp.status === 200) {
        this.checked = resp.data.CHECKED;
        this.submitAllowed = resp.data.SUBMIT_ALLOWED;
        this.openReview = resp.data.OPEN_REVIEW;
        this.openResult = resp.data.OPEN_RESULT;
        this.openFinalResult = resp.data.OPEN_FINAL_RESULT;
        this.all.push.apply(this.all,this.checked);
        this.all.push.apply(this.all,this.submitAllowed);
        this.all.push.apply(this.all,this.openReview);
        this.all.push.apply(this.all,this.openResult);
        this.all.push.apply(this.all,this.openFinalResult);
        this.conferences = this.all;
      } else {
        this.$message.error("Request Error.")
      }
    })
    .catch(error =>{
      console.log(error);
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
</style>
