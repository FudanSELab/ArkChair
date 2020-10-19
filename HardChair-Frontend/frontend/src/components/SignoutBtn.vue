<template>
  <router-link to @click.native="signOut">
    <el-menu-item>
      <em class="el-icon-circle-close"></em>Sign out
    </el-menu-item>
  </router-link>
</template>

<script>
export default {
  name: "signoutbtn",
  data() {
    return {};
  },
  methods: {
    signOut() {
      this.$confirm("Are you sure to sign out?", "Sign Out", {
        confirmButtonText: "Yes",
        cancelButtonText: "No"
      })
        .then(() => {
          this.$store.commit("logout");
          this.$router.push("/login");
          this.$message({
            dangerouslyUseHTMLString: true,
            message: '<strong style="color:teal">You have signed out!</strong>',
            center: true
          });
        })
        .catch(error => {});
    }
  }
};
</script>
