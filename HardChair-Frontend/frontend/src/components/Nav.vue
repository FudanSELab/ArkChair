<template>
  <div id="base_application">
    <div class="navbar-container">
      <nav class="navbar navbar-expand-lg bg-white navbar-light" data-sticky="top">
        <div class="container">
          <!--logo -->
          <a class="navbar-brand fade-page">
            <router-link to="/">ArkChair</router-link>
          </a>

          <el-menu mode="horizontal"  style="border-bottom:0;">
            <!-- Conference opration -->
            <el-submenu index="1">
              <template slot="title">
                <em class="el-icon-collection"></em>Conferences
              </template>
              <router-link to="/conference-home">
                <el-menu-item>
                  <em class="el-icon-menu"></em>Browsing
                </el-menu-item>
              </router-link>
              <router-link to="/conference-application" v-if="isNormalUser">
                <el-menu-item>
                  <em class="el-icon-edit-outline"></em>Application
                </el-menu-item>
              </router-link>
            </el-submenu>

            <!-- login and register button -->
            <el-menu-item v-if="beforeLogin">
              <loginbtn></loginbtn>
            </el-menu-item>
            <el-menu-item v-if="beforeLogin">
              <registerbtn></registerbtn>
            </el-menu-item>

            <!-- Personal info -->
            <el-submenu index="2" v-if="afterLogin">
              <template slot="title">
                <em class="el-icon-user-solid"></em>My ArkChair
                <el-badge :is-dot="isUpdated" class="mark" />
              </template>
              <router-link to="/my-ark-chair" v-if="isNormalUser">
                <el-menu-item>
                  <em class="el-icon-s-order"></em>My ArkChair Center
                </el-menu-item>
              </router-link>
              <router-link to="/verification" v-if="isADMIN">
                <el-menu-item>
                  <em class="el-icon-s-claim"></em>Verification
                  <el-badge :is-dot="isUpdated" class="mark" />
                </el-menu-item>
              </router-link>
              <router-link to="/message-inbox" v-if="isNormalUser">
                <el-menu-item>
                  <em class="el-icon-message"></em>Messages
                  <el-badge :is-dot="isUpdated" class="mark" />
                </el-menu-item>
              </router-link>
              <signoutbtn></signoutbtn>
            </el-submenu>
          </el-menu>
        </div>
      </nav>
    </div>
  </div>
</template>

<script>
import loginbtn from "./LoginBtn";
import registerbtn from "./RegisterBtn";
import signoutbtn from "./SignoutBtn";

export default {
  name: "navbar",
  components: { loginbtn, registerbtn, signoutbtn },
  data() {
    return {
      beforeLogin: true,
      afterLogin: false,
      isNormalUser: true,
      isADMIN: false,
      isUpdated:false,
      activeIndex: "1",
      activeIndex2: "1"
    };
  },
  created() {
    // Control the display of different interface
    if (this.$store.state.token) {
      this.beforeLogin = false;
      this.afterLogin = true;
      // Present user is an admin
      if (this.$store.state.userType === "ADMIN") {
        this.isNormalUser = false;
        this.isADMIN = true;
      }
    }

    // Control the display of red dot
    if(this.$store.state.token){
      // ADMIN
      if(this.$store.state.userType == 'ADMIN'){
        this.$axios.get('/VerifyUpdate',{})
        .then(resp => {
          if(resp.data === true){
            this.isUpdated = true;
          }
        })
        .catch(error => {
          console.log(error);
        })
      }else{
        // USER
        this.$axios.get('/MessageUpdate',{})
        .then(resp => {
          if(resp.data === true){
            this.isUpdated = true;
          }
        })
        .catch(error => {
          console.log(error);
        })
      }
    }
    
  },
  methods: {
  }
};
</script>

<style scoped>
.navbar {
  border-bottom: 0;
  padding: 0;
}
mark,
.mark {
  padding: 0;
  background-color: transparent;
}
</style>
