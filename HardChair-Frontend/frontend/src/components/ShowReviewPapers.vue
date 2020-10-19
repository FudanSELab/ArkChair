<template>
  <div>
    <div v-if="!papers || papers.length == 0">
      <el-card shadow="hover">No paper now!</el-card>
    </div>
    <el-card
      v-else
      shadow="hover"
      class="box-card"
      style="margin-top: 1em;"
      v-for="paper in papers.slice((currentPage- 1)*pageSize,currentPage*pageSize)"
      :key="paper.id"
    >
      <p>
        <span class="itemlabel">
          <em class="el-icon-s-opportunity"></em> Title:
        </span>
        {{paper.title}}
      </p>
      <p>
        <span class="itemlabel">
          <em class="el-icon-s-fold"></em> Summary:
        </span>
        {{paper.summary}}
      </p>
      <p v-if="paper.createdTime">
        <span class="itemlabel">
          <em class="el-icon-date"></em> Upload date:
        </span>
        {{paper.createdTime.substring(0,10)}}
      </p>
      <!-- paper operation -->
      <div class="row">
        <preview class="onPageBtn" :id="paper.id">Preview</preview>
        <download class="onPageBtn" :id="paper.id" :title="paper.title"></download>
        <el-button class="onPageBtn" type="primary" @click="$router.push({path:'/paper/'+paper.id}) ">More</el-button>
      </div>
    </el-card>
    <div class="row">
      <div class="col-xl-6 col-lg-12">
        <el-pagination
          v-if="papers && papers.length > 0"
          hide-on-single-page
          layout="prev, pager, next"
          :page-size="pageSize"
          :current-page.sync="currentPage"
          :total="papers.length"
        >></el-pagination>
      </div>
    </div>
  </div>
</template>

<script>
import download from "./DownloadPaper";
import preview from "./PreviewPaper";

export default {
  name: "ShowPapers",
  components: { download, preview },
  props: ["conferenceId","paper"],
  data() {
    return {
      pageSize: 2,
      currentPage: 1,
      papers: []
    };
  },
  created() {
    if(this.conferenceId){
      this.$axios
      .post("/ReviewPapers", {
        conferenceId: this.conferenceId
      })
      .then(resp => {
        this.papers = resp.data;
      })
      .catch(error => {
        console.log(error);
      });
    }else{
      this.papers = this.paper;
    }
  }
};
</script>

<style scoped>
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
.contentContainer {
  padding: 2em;
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
