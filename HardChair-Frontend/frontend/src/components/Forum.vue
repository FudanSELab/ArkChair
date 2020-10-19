<template>
  <div>
    <!-- reply area -->
    <div v-if="canDiscuss" ref="reply_area">
      <div class="el-icon-edit-outline">Join the discussion here!</div>
      <el-tag
        class="discussInput discussQuoteTag"
        v-if="quoteContent"
        @close="quoteContent='';quoteId=-1"
        closable
      >{{quoteContent}}</el-tag>
      <el-input
        class="discussInput"
        type="textarea"
        autosize
        size="medium"
        v-model="postContent"
        auto-complete="off"
        maxlength="250"
        show-word-limit
        placeholder="What do you think of the essay?"
      ></el-input>
      <br />
      <el-button
        class="discussInput"
        :disabled="submitDisable"
        @click="submitPost"
        size="medium"
        type="primary"
        style="width:100% "
      >Submit</el-button>
    </div>
    <el-card shadow="hover" v-else>Discussion is not available now.</el-card>

    <!-- post area -->
    <div v-if="posts">
      <el-card
        shadow="hover"
        class="box-card"
        style="margin-top: 1em;"
        v-for="(post,index) in posts.slice((currentPage- 1)*pageSize,currentPage*pageSize)"
        :key="index"
      >
        <!-- header -->
        <div slot="header" class="clearfix">
          <span>{{post.username}}</span>
          <el-button
            v-if="canDiscuss"
            style="float: right; padding: 3px 0"
            type="text"
            @click="reply(post)"
          >Reply</el-button>
        </div>
        <el-tag
          class="discussQuoteTag"
          v-if="post.quoteId != -1"
        >{{getQuoteContent(post.quoteId)}}</el-tag>
        <div>{{post.postContent}}</div>
      </el-card>
    </div>

    <br />

    <!-- paging -->
    <el-pagination
      v-if="posts.length>0"
      hide-on-
      single-page
      layout="prev, pager, next"
      :page-size="pageSize"
      :current-page.sync="currentPage"
      :total="posts.length"
    ></el-pagination>
  </div>
</template>

<script>
export default {
  name: "forum",
  props: ["canDiscuss", "posts", "paperId"],
  inject: ["reload"],

  data() {
    return {
      postContent: "",
      quoteId: -1,
      quoteContent: "",

      pageSize: 6,
      currentPage: 1
    };
  },
  computed: {
    submitDisable() {
      return this.postContent == "";
    }
  },
  methods: {
    reply(post) {
      this.quoteId = post.id;
      this.quoteContent = post.username + " : " + post.postContent;
      this.$refs.reply_area.scrollIntoView({
        block: "start",
        inline: "nearest",
        behavior: "smooth"
      });
    },
    getQuoteContent(quoteId) {
      let len = this.posts.length;
      for (let i = 0; i < len; i++) {
        if (this.posts[i].id == quoteId) {
          return (this.posts[i].username + ": " + this.posts[i].postContent);
        }
      }
    },
    notify(content, format) {
      this.$message({
        dangerouslyUseHTMLString: true,
        type: format,
        message: '<strong style="color:teal">' + content + "</strong>",
        center: true
      });
    },
    submitPost() {
      this.$axios
        .post("/SubmitPost", {
          postContent: this.postContent,
          paperId: this.paperId,
          quoteId: this.quoteId
        })
        .then(resp => {
          if (resp.status === 200) {
            switch (resp.data.message) {
              case "success":
                this.notify("Reply successfully!", "success");
                this.reload();
                break;
              case "No Authority":
                this.notify("You don't hava the authority!", "error");
                break;
              case "fail: conference status":
                this.notify(
                  "You cannot submit at this conference stage!",
                  "error"
                );
                break;
            }
          }
        })
        .catch(error => {
          console.log(error);
        });
      this.postContent = "";
      this.quoteContent = "";
      this.quoteId = -1;
    }
  }
};
</script>

<style scoped>
.discussInput {
  margin-top: 1em;
}
.discussQuoteTag {
  white-space: unset;
  height: unset;
}
</style>