<template>
  <div id="base_application" v-title data-title="ArkChair - Conference Application">
    <navbar></navbar>

    <section class="bg-primary header-inner p-0 jarallax position-relative o-hidden" data-overlay>
      <div class="container py-0 layer-2">
        <div class="row my-4 my-md-6 text-light">
          <div class="col-lg-9 col-xl-6">
            <h1 class="display-4">Conference Application</h1>
            <p
              class="lead mb-0"
            >After submission, your application will be reviewed by an admin. Thank you for your patience.</p>
          </div>
        </div>
      </div>
    </section>

    <section>
      <div class="container">
        <div class="row">
          <div class="col-xl-6 col-lg-8">
            <el-form
              @submit.native.prevent
              status-icon
              :model="applicationForm"
              :rules="rules"
              label-position="top"
              v-loading="loading"
              ref="applicationForm"
            >
              <!-- short name -->
              <el-form-item prop="nameAbbreviation" label="Short name">
                <el-input
                  type="text"
                  v-model="applicationForm.nameAbbreviation"
                  auto-complete="off"
                  id="nameAbbreviation"
                  placeholder="Enter the short name of your conference"
                ></el-input>
              </el-form-item>

              <!--full name -->
              <el-form-item prop="fullName" label="Full name">
                <el-input
                  type="text"
                  v-model="applicationForm.fullName"
                  auto-complete="off"
                  id="fullName"
                  placeholder="Enter the full name of your conference"
                ></el-input>
              </el-form-item>

              <!-- topic -->
              <el-form-item prop="topic" label="Topic">
                <el-tag
                  :key="topic"
                  v-for="topic in applicationForm.topic"
                  closable
                  @close="handleClose(topic)"
                >{{topic}}</el-tag>
                <el-input
                  class="input-new-tag"
                  v-if="inputVisible"
                  v-model="inputValue"
                  ref="saveTagInput"
                  size="small"
                  @keyup.enter.native="handleInputConfirm"
                  @blur="handleInputConfirm"
                ></el-input>
                <el-button v-else class="button-new-tag" size="small" @click="showInput">+ New Topic</el-button>
              </el-form-item>

              <!-- location -->
              <el-form-item prop="location" label="Location">
                <el-input
                  type="text"
                  v-model="applicationForm.location"
                  auto-complete="off"
                  id="location"
                  placeholder="Enter the location of the conference"
                ></el-input>
              </el-form-item>              

              <!-- deadline -->
              <el-form-item prop="deadline" label="Submission deadline">
                <el-date-picker
                  :clearable="false"
                  :picker-options="deadlinePickerOptions"
                  v-model="applicationForm.deadline"
                  type="date"
                  placeholder="Pick submission deadline"
                  style="width: 100%"
                ></el-date-picker>
              </el-form-item>

              <!-- result announcement date -->
              <el-form-item prop="resultAnnounceDate" label="Result announcement date">
                <el-date-picker
                  :clearable="false"
                  :picker-options="announcementDatePickerOptions"
                  v-model="applicationForm.resultAnnounceDate"
                  type="date"
                  placeholder="Pick result announcement date"
                  style="width: 100%"
                ></el-date-picker>
              </el-form-item>

              <!-- start & end time -->
              <el-form-item prop="time" label="Start and end dates">
                <el-date-picker
                  :clearable="false"
                  :picker-options="dateRangePickerOptions"
                  v-model="applicationForm.time"
                  type="daterange"
                  range-separator="to"
                  start-placeholder="Start date"
                  end-placeholder="End date"
                  style="width:100%"
                ></el-date-picker>
              </el-form-item>

              <br />

              <!-- submit button -->
              <el-form-item style="width: 100%">
                <el-button
                  :disabled="isDisabled"
                  type="primary"
                  style="width: 100%"
                  v-on:click="apply()"
                >Submit application</el-button>
              </el-form-item>
            </el-form>
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
  name: "ConferenceApplication",
  components: { navbar, footerbar },
  data: function() {
    // If the deadline or the announcement date has been set, the date range should include it
    // const containDDLAndAnnounceDate = (rule,value,callback)=> {
    //   this.isTimeValid = false;
    //   if (this.applicationForm.deadline) {
    //     if(value[0] > this.applicationForm.deadline || value[1] < this.applicationForm.deadline){
    //       this.applicationForm.time = "";
    //       callback(new Error("The date range should contain the deadline you have chosen"));
    //       this.changeDisabled();
    //       return;
    //     }
    //   }
    //   if(this.applicationForm.resultAnnounceDate){
    //     if(value[0] > this.applicationForm.resultAnnounceDate || value[1] < this.applicationForm.resultAnnounceDate){
    //       this.applicationForm.time = "";
    //       callback(new Error("The date range should contain the result announcement date you have chosen"));
    //       this.changeDisabled();
    //       return;
    //     }
    //   }
    //   if(value!=='') {
    //     this.isTimeValid = true;
    //   }
    //   callback();
    //   this.changeDisabled();
    // }

    return {
      // Topic Tag
      inputVisible: false,
      inputValue: "",

      //Picker options
      dateRangePickerOptions: {
        disabledDate: time => {
          // The date range should start after today, announcement date and ddl
          const oneDayTime = 24 * 3600 * 1000;
          if (this.applicationForm.resultAnnounceDate) {
            return time.getTime() < this.applicationForm.resultAnnounceDate;
          } else {
            if (this.applicationForm.deadline) {
              return time.getTime() < this.applicationForm.deadline;
            } else {
              return time.getTime() < Date.now() - oneDayTime;
            }
          }
        }
      },

      deadlinePickerOptions: {
        disabledDate: time => {
          // 1. If the announcement date has been set, the deadline should before it.
          // 2. If the date range has been set, the deadline should be before it.
          // 3. If not, make sure it's a date after today
          const oneDayTime = 24 * 3600 * 1000;
          if (this.applicationForm.resultAnnounceDate) {
            return time.getTime() < Date.now() - oneDayTime || time.getTime() > this.applicationForm.resultAnnounceDate;
          } else {
            if (this.applicationForm.time) {
              return time.getTime() < Date.now() - oneDayTime || time.getTime() > this.applicationForm.time[0];
            } else {
              return time.getTime() < Date.now() - oneDayTime;
            }
          }
        }
      },

      announcementDatePickerOptions: {
        disabledDate: time => {
          // 1. If the date range has been set, result announcement day should be before it.
          // 2. If the ddl has been set, the result announcement day should be after it.
          // 3. If not, make sure it's a date after today
          const oneDayTime = 24 * 3600 * 1000;
          if (this.applicationForm.time) {
            if (this.applicationForm.deadline) {
              return (
                time.getTime() < this.applicationForm.deadline  ||
                time.getTime() > this.applicationForm.time[0]
              );
            } else {
              return (
                time.getTime() < Date.now() - oneDayTime ||
                time.getTime() > this.applicationForm.time[0]
              );
            }
          } else {
            if (this.applicationForm.deadline) {
              return time.getTime() < this.applicationForm.deadline;
            } else {
              return time.getTime() < Date.now() - oneDayTime;
            }
          }
        }
      },

      applicationForm: {
        nameAbbreviation: "",
        fullName: "",
        topic: [],
        location: "",
        time: "",
        deadline: "",
        resultAnnounceDate: ""
      },
      rules: {
        nameAbbreviation: [
          {
            required: true,
            message: "Short name of conference is required",
            trigger: "blur"
          },
        ],
        fullName: [
          {
            required: true,
            message: "Full name of conference is required",
            trigger: "blur"
          },
        ],
        topic: [
          {
            type: "array",
            required: true,
            message: "Topics are required",
            trigger: "blur"
          },
        ],
        location: [
          {
            required: true,
            message: "Location of conference is required",
            trigger: "blur"
          }
        ],
        time: [
          {
            required: true,
            message: "Start and end dates of conference are required",
            trigger: "blur"
          },
        ],
        deadline: [
          {
            required: true,
            message: "Submission deadline is required",
            trigger: "blur"
          },
        ],
        resultAnnounceDate: [
          {
            required: true,
            message: "Result announcement date is required",
            trigger: "blur"
          },
        ]
      },
      loading: false
    };
  },
  computed:{
    isDisabled(){
      return this.applicationForm.nameAbbreviation === "" ||
      this.applicationForm.fullName === "" ||
      this.applicationForm.topic.length < 1 ||
      this.applicationForm.time === "" ||
      this.applicationForm.location === "" ||
      this.applicationForm.deadline === "" ||
      this.applicationForm.resultAnnounceDate === "";
    }
  },
  methods: {
    // manage topic tag
    handleClose(tag) {
      this.applicationForm.topic.splice(
        this.applicationForm.topic.indexOf(tag),
        1
      );
      this.$refs.applicationForm.validateField("topic");
      this.changeDisabled();
    },

    showInput() {
      this.inputVisible = true;
      this.$nextTick(_ => {
        this.$refs.saveTagInput.$refs.input.focus();
      });
    },

    handleInputConfirm() {
      let inputValue = this.inputValue;
      if (inputValue && this.applicationForm.topic.indexOf(inputValue) == -1) {
        this.applicationForm.topic.push(inputValue);
      }
      this.inputVisible = false;
      this.inputValue = "";
    },
    //~ manage topic tag

    apply() {
      //In case of some bug, still validate before submit
      this.$refs.applicationForm.validate(valid => {
        if (valid) {
          this.loading = true;
          this.$axios
            .post("/ConferenceApplication", {
              nameAbbreviation: this.applicationForm.nameAbbreviation,
              fullName: this.applicationForm.fullName,
              topic: this.applicationForm.topic,
              time: this.applicationForm.time,
              location: this.applicationForm.location,
              deadline: this.applicationForm.deadline,
              resultAnnounceDate: this.applicationForm.resultAnnounceDate
            })
            .then(resp => {
              // 根据后端的返回数据修改
              if (resp.status === 200 && resp.data.hasOwnProperty("id")) {
                this.loading = false;
                this.$message({
                  type: "success",
                  center: true,
                  dangerouslyUseHTMLString: true,
                  message:
                    "<strong style='color:teal'>Your application will be reviewed by an admin. Thank you for your patience.</strong>"
                });
                this.$router.replace("/");
              } else {
                this.$message.error("Application failed. Please sign in first");
              }
            })
            .catch(error => {
              this.loading = false;
              console.log(error);
              this.$message.error("Application failed");
            });
        } else {
          this.$message.error("Wrong submit! Please check the form.");
        }
      });
    },
  }
};
</script>

<style scoped>
section {
  padding: 2em;
}
.el-tag {
  margin-right: 5px;
}

.input-new-tag {
  width: 103px;
  margin-right: 5px;
  vertical-align: bottom;
}
</style>
