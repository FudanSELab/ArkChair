<template>
<el-form     
  @submit.native.prevent
  status-icon
  :model="reviewForm"
  :rules="rules"
  label-position="top"
  ref="reviewForm"
>
  <!-- score -->
  <el-form-item prop="score" label="Score" class="is-required">
    <el-rate
      v-model="reviewForm.score"
      show-text
      id="score"
      :max="max"
      :texts="texts"
      :colors="colors"
    ></el-rate>
  </el-form-item>

  <!-- comment -->
  <el-form-item prop="comment" label="Comment">
    <el-input
      type="textarea"
      autosize
      v-model="reviewForm.comment"
      auto-complete="off"
      id="comment"
      placeholder="Your comment on this paper"
    ></el-input>
  </el-form-item>

  <!-- confidence -->
  <el-form-item prop="confidence" label="Confidence">
    <el-radio-group v-model="reviewForm.confidence">
      <el-radio-button label="Very low"></el-radio-button>
      <el-radio-button label="Low"></el-radio-button>
      <el-radio-button label="High"></el-radio-button>
      <el-radio-button label="Very High"></el-radio-button>
    </el-radio-group>
  </el-form-item>

  <br />

  <!-- submit button -->
  <el-form-item v-if = "!isEdit">
    <el-button
      native-type="submit"
      type="primary"
      v-on:click="Submit()"
      :disabled="reviewDisabled"
    >Submit Review Results</el-button>
  </el-form-item>

  <!-- modify button -->
  <el-form-item v-else>
    <el-button
      native-type="submit"
      type="primary"
      v-on:click="confirm()"
      :disabled="reviewDisabled"
    >Submit new Result</el-button>
    <el-button
      type="primary"
      v-on:click="confirm()"
      :disabled="reviewDisabled"
    >Do not Modify</el-button>
  </el-form-item>
</el-form>
</template>
<script>
export default {
    name: "ReviewPaper",
    props:['reviewResult','id'],
    inject: ["reload"],
    data(){
      return{
          isEdit:false,
          reviewForm: {
            score: null,
            comment: "",
            confidence: ""
          },
          rules: {
            score: [
              {
                validator: (rule, value, callback) => {
                  if (!value) {
                    callback(new Error("Score is required"));
                  }                  
                  callback();
                },
                trigger: "blur"
              }
            ],
            comment: [{ required: true, trigger: "blur" }],
            confidence: [{ required: true, trigger: "blur" }]
          },
          // el-rate
          texts: [
            " -2 ( reject )",
            " -1 ( week reject )",
            " 1 ( weak accept )",
            " 2 ( accept )"
          ],
          max: 4,
          colors: ["#99A9BF", "#F7BA2A", "#FF9900"]
      }      
    },
    computed: {
      reviewDisabled() {
        let tmp = this.reviewForm;
        return !tmp.score || tmp.comment == "" || tmp.confidence == "";
      }
    },
    methods:{      
      Submit() {
        let tmp = this.reviewForm;
        this.$axios
          .post("/SubmitReviewResult", {
            paperId: this.id,
            score: tmp.score,
            comment: tmp.comment,
            confidence: tmp.confidence
          })
          .then(resp => {
            if (resp.status === 200) {            
              this.notify("Submit success!","success");
              this.reload();
            }
          })
          .catch(error => {
            console.log(error);
          });
      },
      confirm(){
        this.$confirm("Are you sure about your result?", "Confirm", {
            confirmButtonText: "Yes",
            cancelButtonText: "No"
        })
        .then(() => {
          this.$axios
            .post('/ReviseOrConfirmReviewResult',{
              reviewResultId:this.reviewForm.id,
              score: this.reviewForm.score,
              comment: this.reviewForm.comment,
              confidence: this.reviewForm.confidence                
            })
            .then(resp => {
              if (resp.status === 200) {            
                switch(resp.data.message){
                  case "success":
                    this.notify("Confirm successfully!","success");
                    this.reload();
                    break;
                  case "Please confirm or revise the result after you have discussed with other PC MEMBER":
                    this.notify("No discussion yet!","error");
                    break;
                  case "you have already confirmed or revised the result!!":
                    this.notify("You have already confirmed or revised the result!","error");
                    break;
                  case "No Authority":
                    this.notify("You don't have the authority!","error");
                    break;
                }
              }
            })
            .catch(error => {
              console.log(error);
            });
          })
        .catch(error => {console.log(error)});
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
     created(){       
       if(this.reviewResult != null){
         this.isEdit = true;
         this.reviewForm = this.reviewResult;
         if(this.reviewForm.score < 0){
           this.reviewForm.score +=3;
         }else{
           this.reviewForm.score += 2;
         }
       }
       
       
     }
}
</script>