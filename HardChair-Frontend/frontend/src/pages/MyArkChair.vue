<template>
  <div id="base_verification" v-title data-title="ArkChair - My ArkChair Center">
    <navbar></navbar>

    <section class="bg-primary header-inner p-0 jarallax position-relative o-hidden" data-overlay>
      <div class="container py-0 layer-2">
        <div class="row my-4 my-md-6 text-light">
          <div class="col-lg-9 col-xl-6">
            <h1 class="display-4">My ArkChair Center</h1>
          </div>
        </div>
      </div>
    </section>

    <div class="contentContainer">
      <section>
        <div class="container">
          <div class="row">
            <div class="col-xl-8 col-lg-12">
              <div>
                <h2>
                  <em class="el-icon-info"></em> My Info
                </h2>
                <div class="infoitem">
                  <span class="itemlabel">
                    <em class="el-icon-user"></em> Username:
                  </span>
                  {{user.username}}
                </div>
                <div class="infoitem">
                  <span class="itemlabel">
                    <em class="el-icon-s-custom"></em> Real name:
                  </span>
                  {{user.fullname}}
                </div>
                <div class="infoitem">
                  <span class="itemlabel">
                    <em class="el-icon-office-building"></em> Organization:
                  </span>
                  {{user.organization}}
                </div>
                <div class="infoitem">
                  <span class="itemlabel">
                    <em class="el-icon-map-location"></em> Region:
                  </span>
                  {{user.region}}
                </div>
                <div class="infoitem">
                  <span class="itemlabel">
                    <em class="el-icon-message"></em> Email:
                  </span>
                  {{user.email}}
                </div>
              </div>
            </div>
          </div>
        </div>
      </section>

      <section>
        <div class="container">
          <div class="row">
            <div class="col-xl-8 col-lg-12">
              <h2>
                <em class="el-icon-s-management"></em> My Conferences
              </h2>
              <div v-if="noMeeting">
                <el-card shadow="hover">No related conference now!</el-card>
              </div>
              <el-card
                v-else
                shadow="hover"
                class="box-card"
                style="margin-top: 1em;"
                v-for="conference in conferences.slice((currentPage- 1)*pageSize,currentPage*pageSize)"
                :key="conference.id"
              >
                <div slot="header" class="clearfix">
                  <span style="font-weight: bold">{{conference[0].nameAbbreviation}}</span>
                  <router-link
                    :to="'conference-detail/'+conference[0].id"
                    style="float: right; padding: 3px 0"
                  >View details</router-link>
                </div>
                <div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-chat-line-round"></em> Full name:
                    </span>
                    {{conference[0].fullName}}
                  </div>
                  <div>
                    <span class="itemlabel">
                      <em class="el-icon-s-flag"></em> Status:
                    </span>
                    <conferenceStatus :status = "conference[0].status"></conferenceStatus>
                  </div>
                </div>
              </el-card>
            </div>
          </div>
          <br />

          <div class="row">
            <div class="col-xl-6 col-lg-12">
              <el-pagination
                hide-on-single-page
                layout="prev, pager, next"
                :page-size="pageSize"
                @current-change="pageChange"
                :current-page.sync="currentPage"
                :total="conferences.length"
              >></el-pagination>
            </div>
          </div>
        </div>
      </section>
    </div>

    <footerbar></footerbar>
  </div>
</template>

<script>
import navbar from "../components/Nav";
import footerbar from "../components/Footer";
import conferenceStatus from "../components/ParseConferenceStatus";

export default {
  name: "MyArkChair",
  components: { navbar, footerbar,conferenceStatus },
  data() {
    return {
      user: {},
      conferences: [],
      pageSize: 6,
      currentPage: 1,
      noMeeting: false
    };
  },
  methods: {
    pageChange() {
      this.currentPage = currentPage;
    },
  },
  created() {
    // Get information of conferences that relate to the present user
    this.$axios
      .get("/Profile", {})
      .then(resp => {
        if (resp.status === 200) {
          if (resp.data[1].length == 0) {
            this.noMeeting = true;
          } else {
            this.conferences = resp.data[1];
          }
          this.user = resp.data[0];
        } else {
          this.$message.error("Request Error.");
        }
      })
      .catch(error => {
        console.log(error);
      });
  }
};
</script>

<style scoped>
.itemlabel,
h2 {
  color: #3755be;
  font-weight: bold;
}
.contentContainer {
  padding: 2em;
}
section {
  padding: 0.5em;
}
.infoitem {
  margin-top: 1em;
  margin-bottom: 1em;
}
.onPageBtn {
  margin-left: 12px;
}
</style>
