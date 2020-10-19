<template>
  <div id="base_login" v-title data-title="ArkChair - Login">
    <section class="row no-gutters min-vh-100 p-0">
      <div class="col-lg-4 bg-primary-3 d-flex justify-content-end">
        <img src="../assets/img/amiya.png" alt="Image" class="bg-image" />
        <div class="divider divider-vertical d-none d-lg-block">
          <svg
            width="100%"
            height="100%"
            version="1.1"
            viewBox="0 0 100 100"
            xmlns="http://www.w3.org/2000/svg"
            xmlns:xlink="http://www.w3.org/1999/xlink"
            preserveAspectRatio="none"
          >
            <path
              d="M0,0 L100,0 L100,100 L0,100 C66.6666667,83.3333333 100,66.6666667 100,50 C100,33.3333333 66.6666667,16.6666667 0,0 Z"
              fill="#ffffff"
            />
          </svg>
        </div>
      </div>
      <div class="col px-5 position-relative d-flex align-items-center">
        <div class="row justify-content-center w-100">
          <div class="col-md-8 col-lg-7 col-xl-6">
            <div class="text-center mb-4">
              <h1 class="mb-1">Welcome back</h1>
              <span>Into the world of <router-link to="/">ArkChair</router-link></span>
            </div>
            <el-form
              @submit.native.prevent
              status-icon
              :model="loginForm"
              :rules="rules"
              class="login_container"
              label-position="left"
              label-width="0px"
              v-loading="loading"
            >

              <el-form-item prop="username" size="medium" >
                <el-input
                  size="medium"
                  type="text"
                  v-model="loginForm.username"
                  auto-complete="off"
                  placeholder="Username"
                ></el-input>
              </el-form-item>

              <el-form-item prop="password" size="medium">
                <el-input
                  size="medium"
                  type="password"
                  v-model="loginForm.password"
                  auto-complete="off"
                  placeholder="Password"
                ></el-input>
              </el-form-item>

              <el-form-item size="medium">
                <el-button
                  native-type="submit"
                  :disabled ="isDisabled"
                  size="medium"
                  type="primary"
                  style="width:100% "
                  v-on:click="login"
                >Sign In</el-button>
              </el-form-item>
            </el-form>

            <div class="text-center text-small text-muted">
              <span>
                Don't have an account yet?
                <router-link to="register">Create one</router-link>
              </span>
            </div>
          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
  export default {
    name: "Login",
    data() {
      return {
        loginForm: {
          username: "",
          password: ""
        },
        rules: {
          username: [
            {required:true,message:"Username is required",blur:"change"},
            {pattern:/^[a-zA-Z-][a-zA-Z0-9-_]{4,31}$/,message:"Invalid username",blur:"change"}
          ],
          password: [
            {required:true, message:"Password is required", blur:"change"},
            {pattern:/^[\w-]{6,32}$/, message:"Invalid password",blur:"change"},
          ]
        },
        loading: false,
      };
    },
    computed:{
      isDisabled(){
        return !(/^[a-zA-Z-][a-zA-Z0-9-_]{4,31}$/.test(this.loginForm.username) && /^[\w-]{6,32}$/.test(this.loginForm.password));
      }
    },
    methods: {
      login() {
        // Turn to loading mode when the form is submitted,and come back when getting response
        this.loading = true;
        this.$axios
          .post("/login", {
            username: this.loginForm.username,
            password: this.loginForm.password
          })
          .then(resp => {
            if (resp.status === 200 && resp.data.hasOwnProperty("token")) {
              //Save token
              this.$store.commit("login", resp.data);
              this.$message({
                dangerouslyUseHTMLString: true,
                type:'success',
                message: '<strong style="color:teal">Welcome back!</strong>',
                center:true
              });
              this.$router.replace({ path: "/" });            
            } else {
              this.errorNotification();
              this.loading = false;
            }
          })
          .catch(error => {
            console.log(error);
            this.errorNotification();
            this.loading = false;
          });
      },
      errorNotification(){
        this.$notify({
          type:'error',
          dangerouslyUseHTMLString: true,
          title: 'Login error',
          message: '<strong style="color:teal">Please check your username and password or try again later!</strong>'
        });
      },
    }
  };
</script>
