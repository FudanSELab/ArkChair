<template>
<el-form
  @submit.native.prevent
  status-icon
  :model="paperForm"
  :rules="rules"
  label-position="top"
  ref="paperForm"
  v-loading="loading"
>

  <!-- title -->
  <el-form-item prop="title" label="Title">
    <el-input
      type="text"
      v-model="paperForm.title"
      auto-complete="off"
      id="title"
      placeholder="Title of your paper"
    ></el-input>
  </el-form-item>

  <!-- summary -->
  <el-form-item prop="summary" label="Summary">
    <el-input
      type="textarea"
      autosize
      v-model="paperForm.summary"
      auto-complete="off"
      id="summary"
      placeholder="Summary of your paper"
    ></el-input>
  </el-form-item>

  <!-- topic -->
  <el-form-item prop="topic" label="Topic" class="is-required">
    <el-checkbox-group v-model="paperForm.topics" v-if="paperForm.topics && conferenceTopics">
      <el-checkbox
        class="checkboxes"
        v-for="(topic,index) in conferenceTopics"
        :key="index"
        :label="topic"
        border
      ></el-checkbox>
    </el-checkbox-group>
  </el-form-item>

  <!-- author -->
  <el-form-item prop="author" label="Author" class="is-required">
    <el-button class="button-new-tag" @click="showAddAuthorForm">+ New Author</el-button>
    <span v-if="paperForm.authors && paperForm.authors.length >0" >&nbsp;Drag to sort</span>

    <draggable v-model="paperForm.authors">
      <el-card
        shadow="hover"
        class="box-card"
        style="margin-top: 1em;"
        v-for="(author,index) in paperForm.authors"
        :key="index"
      >
        <div slot="header" class="clearfix" style="line-height: 1">
          <span
            style="font-weight: bold"
          >{{ (index+1) + (['st', 'nd', 'rd'][(index+1) &lt; 20 ? index : (index+1) % 10 - 1] || 'th')}} Author
          </span>
          <el-button
            style="float: right; padding: 3px 0"
            type="text"
            @click="deleteAuthor(index)"
          >Delete</el-button>
        </div>
        <div>
          <span class="itemlabel">
            <em class="el-icon-s-custom"></em> Name:
          </span>
          {{author.name}}
        </div>
        <div>
          <span class="itemlabel">
            <em class="el-icon-office-building"></em> Organization:
          </span>
          {{author.organization}}
        </div>
        <div>
          <span class="itemlabel">
            <em class="el-icon-map-location"></em> Region:
          </span>
          {{author.region}}
        </div>
        <div>
          <span class="itemlabel">
            <em class="el-icon-message"></em> Email:
          </span>
          {{author.email}}
        </div>
      </el-card>
    </draggable>
  </el-form-item>

  <el-form-item prop="file" label="Upload File" class="is-required">
    <el-upload
      ref="upload"
      drag
      action
      :auto-upload="false"
      :limit="1"
      :http-request="upload"
      accept="application/pdf"
      :before-upload="onBeforeUpload"
      :on-exceed="handleExceed"
      :on-change="handleChange"
      :file-list="files"
    >
      <em class="el-icon-upload"></em>
      <div class="el-upload__text">
        Drag file here to upload, or
        <em>click here</em>
      </div>
      <div class="el-upload__tip" slot="tip">Please upload one PDF file only.</div>
    </el-upload>
  </el-form-item>

  <br />

  <!-- submit button -->
  <el-form-item>
    <el-button type="primary" v-on:click="Submit('paperForm')">Submit</el-button>
  </el-form-item>

  <el-dialog
    title="Add new Author"
    :visible.sync="addAuthorVisible"
    :show-close="false"
    :close-on-click-modal="false"
    :close-on-press-escape="false"
  >
    <el-form :model="authorForm" status-icon :rules="authorRules" ref="authorForm" @keyup.enter.native="enterAdd()">
      <el-form-item label="Name" prop="name">
        <el-input v-model="authorForm.name" autocomplete="off" ref="authorName"></el-input>
      </el-form-item>
      <el-form-item label="Organization" prop="organization">
        <el-input v-model="authorForm.organization" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="Region" prop="region">
        <el-input v-model="authorForm.region" autocomplete="off"></el-input>
      </el-form-item>
      <el-form-item label="Email" prop="email">
        <el-input v-model="authorForm.email" autocomplete="off"></el-input>
      </el-form-item>
    </el-form>
    <div slot="footer" class="dialog-footer">
      <el-button @click="cancelAddAuthor">Cancel</el-button>
      <el-button type="primary" @click="addAuthor" :disabled="addButtonDisable">Add</el-button>
    </div>
  </el-dialog>

</el-form>
</template>

<script>
import draggable from "vuedraggable";

export default {
  name: "SubmitPaper",
  components:{draggable},
  props:['paper','topics','conferenceId'],
  inject: ["reload"],
  data(){
      return{
        file:null,
        conferenceTopics:"",
        // Upload form
        paperForm: {
          title:"",
          summary:"",
          topics:[],
          authors:[]
        },
        rules:{
            title: [
                {
                  required: true,
                  message: "Title of paper is required",
                  trigger: "blur"
                },
                {
                  max: 50,
                  message: "Title can't be more than 50 characters",
                  trigger: "change"
                }
              ],
              summary: [
                {
                  required: true,
                  message: "Summary of paper is required",
                  trigger: "blur"
                },
                {
                  max: 800,
                  message: "Summary can't be more than 800 characters",
                  trigger: "change"
                }
              ],
              topic: [
                {
                  validator: (rule, value, callback) => {
                    if (this.paperForm.topics.length == 0) {
                      callback(new Error("Please choose at least one topic."));
                    }
                    callback();
                  },
                  trigger: "change"
                }
              ],
              author: [
                {
                  validator: (rule, value, callback) => {
                    if (this.paperForm.authors.length == 0) {
                      callback(new Error("Please enter at least one author"));
                    }
                    callback();
                  },
                  trigger: "blur"
                }
              ]
        },
        addAuthorVisible:false,
        files:[{name:"",url:""}],
        loading:false,
        fileChange:false,

        isEdit:false,

        // add author form
        authorForm: {
          name: "",
          organization: "",
          region: "",
          email: ""
        },
        authorRules: {
          name: [
            { required: true, message: "Name is required.", trigger: "blur" }
          ],
          organization: [
              {required: true,message: "Organization is required.",trigger: "blur"}
          ],
          region: [
            { required: true, message: "Region is required.", trigger: "blur" }
          ],
          email: [
            { required: true, message: "Email is required.", trigger: "blur" },
            { type: "email", message: "Invalid email.", trigger: "blur" }
          ]
        }
      }
  },
  methods:{
      enterAdd(){
          if(!this.addButtonDisable){
            this.addAuthor();
          }
      },
      showAddAuthorForm(){
        this.addAuthorVisible = true;
        this.$nextTick(_ => {
            this.$refs.authorName.focus();
            this.$refs["authorForm"].resetFields();
        });
      },
      addAuthor() {
        let author = {
          name: this.authorForm.name,
          organization: this.authorForm.organization,
          region: this.authorForm.region,
          email: this.authorForm.email
        };
        this.paperForm.authors.push(author);
        this.cancelAddAuthor();
      },
      cancelAddAuthor() {
        this.addAuthorVisible = false;
        this.$refs["paperForm"].validateField("author");
      },
      deleteAuthor(index){
        this.paperForm.authors.splice(index, 1);
        this.$refs["paperForm"].validateField("author");
      },

      // upload file
      handleExceed() {
        this.$message({
          type: "warning",
          message: "Can't upload more than 1 file!"
        });
      },
      handleChange(file){
          this.file = file;
          this.fileChange = true;
      },
      onBeforeUpload(file) {
        const isPDF = file.type === "application/pdf";
        if (!isPDF) {
          this.$message.error("Please upload a pdf file!");
        }
        return isPDF;
      },
      upload(params) {
        this.file = params.file;
        return;
      },
      
      Submit(formName) {
        this.$refs[formName].validate(valid => {
          if (valid) {
              // First contribution but no paper file
            if(!this.isEdit && !this.file.hasOwnProperty("name")){
                this.$message({
                  type: "warning",
                  center: true,
                  dangerouslyUseHTMLString: true,
                  message:
                    "<strong style='color:teal'>Please upload your paper file!</strong>"
                });
                return;
            }
            this.loading = true;
            this.$refs["upload"].submit();
            
            var data = new FormData(); 
            var paperTmp = this.paperForm;
            data.append("title", paperTmp.title);
            data.append("summary", paperTmp.summary);
            data.append("topic", paperTmp.topics);
            data.append("conferenceId",this.conferenceId);
            var authors = [];
            var len = paperTmp.authors.length;
            for (let i = 0; i < len; i++) {
                let author = paperTmp.authors[i];
                authors.push(author.name);
                authors.push(author.organization);
                authors.push(author.region);
                authors.push(author.email);
            }
            data.append("authors", authors);
            data.append("file", this.file);
            data.append("paperId", paperTmp.id);

            if( this.isEdit && this.file !== null){// File was changed
                data.append("reviseFile","reviseFile");
            }else{// Not edit or file not change
                data.append("reviseFile","");
            }

            var config = {
              headers: { "Content-Type": "multipart/form-data" }
            };

            var url = this.isEdit?'/RevisePaper':'/SubmitPaper';

            this.$axios
            .post(url, data, config)
            .then(resp => {
              if (resp.status === 200) {
                this.reload();
                this.$message({
                  type: "success",
                  center: true,
                  dangerouslyUseHTMLString: true,
                  message:
                    "<strong style='color:teal'>Submission successful!</strong>"
                });
                this.reload();
              }
            })
            .catch(error => {
              console.log(error);
            });
          } else {
            this.$message.error("Wrong submit! Please check the form.");
            this.loading = false;
          }
        });
      },
    },
    computed: {
      addButtonDisable() {
        return (
          this.authorForm.name == "" ||
          this.authorForm.organization == "" ||
          this.authorForm.region == "" ||
          this.authorForm.email == "" ||
          !/^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(
            this.authorForm.email
          )
        );
      }
    },
    created(){
        // Edit
        if(this.paper !== undefined){
            this.paperForm = this.paper;
            this.isEdit = true;
            this.files[0].name = this.paperForm.title+".pdf";
            this.conferenceTopics = this.topics[0].topic.split(',');            
            this.paperForm.authors.sort(function(a, b){return a.orderOfAuthor - b.orderOfAuthor});
            return;
        }

        // First upload
        this.conferenceTopics = this.topics.split(',');        
        this.files=[];
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
