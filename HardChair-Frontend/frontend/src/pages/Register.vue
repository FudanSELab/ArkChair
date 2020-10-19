<template>
  <div id="base_register" v-title data-title="ArkChair - Register">
    <section class="row no-gutters min-vh-100 p-0">
      <div class="col-lg-4 bg-primary-3 d-flex justify-content-end">
        <img src="../assets/img/sora.png" alt="Image" class="bg-image" />
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
              <h1 class="mb-1">Register</h1>
              <span>Into the world of <router-link to="/">ArkChair</router-link></span>
            </div>
            <el-form
              @submit.native.prevent
              status-icon
              :model="registerForm"
              :rules="rules"
              class="register_container"
              label-position="left"
              label-width="0px"
              v-loading="loading"
              ref="registerForm"
            >
              <!-- username -->
              <el-form-item prop="username" size="medium">
                <el-popover
                  placement="right"
                  width="250"
                  trigger="focus"
                  >
                  <div style="font-size:small">
                    <p>* Use <strong>numbers</strong>, <strong>letters</strong>, <strong>-</strong> and <strong>_</strong></p>
                    <p>* Start with <strong>letters</strong> or <strong>-</strong></p>
                    <p>* <strong>5</strong> and <strong>32</strong> characters in length</p>
                  </div>
                  <el-input
                    slot="reference"
                    size="medium"
                    type="text"
                    v-model="registerForm.username"
                    auto-complete="off"
                    placeholder="Username"
                  ></el-input>
                </el-popover>
              </el-form-item>

              <!-- password -->
              <el-form-item prop="password" size="medium">
                <el-popover
                  placement="right"
                  width="250"
                  trigger="focus"
                >
                  <div style="font-size:small">
                    <p>* Contain <strong>at least two types</strong> of <strong>numbers</strong>, <strong>letters</strong>, and the two characters <strong>-</strong> and <strong>_</strong></p>
                    <p>* Should't contain username.</p>
                    <p>* <strong>6</strong> - <strong>32</strong> characters in length</p>
                  </div>
                  <el-input
                    slot="reference"
                    size="medium"
                    type="password"
                    v-model="registerForm.password"
                    auto-complete="off"
                    placeholder="Password"
                  ></el-input>
                </el-popover>
              </el-form-item>

              <!-- email -->
              <el-form-item prop="email" size="medium">
                <el-input size="medium"
                  type="email"
                  v-model="registerForm.email"
                  auto-complete="off"
                  placeholder="Your e-mail"
                ></el-input>
              </el-form-item>

              <!-- fullname -->
              <el-form-item prop="fullname" size="medium">
                <el-input size="medium"
                  type="fullname"
                  v-model="registerForm.fullname"
                  auto-complete="off"
                  placeholder="Your real name"
                ></el-input>
              </el-form-item>

              <!-- organization -->
              <el-form-item prop="organization" size="medium">
                <el-input size="medium"
                  type="organization"
                  v-model="registerForm.organization"
                  auto-complete="off"
                  placeholder="Your organization"
                ></el-input>
              </el-form-item>

              <!-- region -->
              <el-form-item prop="region" size="medium">
                <el-input size="medium"
                  type="region"
                  v-model="registerForm.region"
                  auto-complete="off"
                  placeholder="Your region"
                ></el-input>
              </el-form-item>

              <!-- register button-->
              <el-form-item style="width: 100%">
                <el-button
                  native-type="submit"
                  :disabled="isDisabled"
                  size="medium"
                  type="primary"
                  style="width:100%"
                  v-on:click="register()"
                >Register</el-button>
              </el-form-item>
              
              <div class="text-center text-small text-muted">
                <span>
                  Already have an account?
                  <router-link to="login">Sign in</router-link>
                </span>
              </div>
            </el-form>

          </div>
        </div>
      </div>
    </section>
  </div>
</template>

<script>
export default {
  name: "Register",
  data() {
    return {
      registerForm: {
        username: "",
        password: "",
        email: "",
        fullname: "",
        organization: "",
        region:""
      },
      rules: {
        username: [
          {required:true, message:"Username is required", trigger:"change"},
          {pattern:/^[a-zA-Z-][a-zA-Z0-9-_]{4,31}$/, message:"Invalid username",trigger:"change"},
          ],
        password: [
          {required:true,message:"Password is required",trigger:"change"},
          {
            validator:(rule,value,callback)=>{
              if(value.indexOf(this.registerForm.username)!==-1) {
                callback(new Error('password cannot contain the username'));
              }
              callback();
            },
            message:"password cannot contain the username",
            trigger:"change"
          },
          {pattern: /(?!^(\d+|[a-zA-Z]+|[-_]+)$)^[\w-]{6,32}$/, message:"Invalid password", trigger:"change"},          
          ],
        email: [
          {required:true, message:"Email is required",trigger:"change"},
          {pattern: /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/, message:"Invalid email", trigger:"change"},
          ],
        fullname: [{required:true, message:"Fullname is required",trigger:"change"}],
        organization: [
          {required:true, message:"Organization is required", trigger:"change"}],
        region: [{required:true, message:"Region is required", trigger:"change"}]
      },
      loading: false
    };
  },
  computed:{
    isDisabled(){
      var form = this.registerForm;
      return !(form.username && form.password && form.email && form.fullname && form.organization && form.region
              && /^[a-zA-Z-][a-zA-Z0-9-_]{4,31}$/.test(form.username)
              && /(?!^(\d+|[a-zA-Z]+|[-_]+)$)^[\w-]{6,32}$/.test(form.password)
              && form.password.indexOf(form.username)==-1
              && /^\w+([-+.]\w+)*@\w+([-.]\w+)*\.\w+([-.]\w+)*$/.test(form.email)
              );
    }
  },

  methods: {
    register() {
      this.loading = true;
      this.$axios
        .post("/register", {
          username: this.registerForm.username,
          password: this.registerForm.password,
          email:this.registerForm.email,
          fullname: this.registerForm.fullname,
          organization:this.registerForm.organization,
          region:this.registerForm.region
        })
        .then(resp => {
          if (resp.status === 200 && resp.data.hasOwnProperty("id")) {
            this.$message({
              dangerouslyUseHTMLString: true,
              type:'success',
              message: '<strong style="color:teal">Register successfully! You can sign in now.</strong>',
              center:true
            });
            this.$router.replace("/login");
          } else {
            this.errorNotification();
            this.loading=false;
          }
        })
        .catch(error => {
          console.log(error);
          this.errorNotification();
          this.loading=false;
        });
    },
    errorNotification(){
      this.$notify.error({
        title: 'Register error',
        message:'User name is already registered. Please try another one'
      });
    }
  }
};
</script>

<style scoped>
  strong{
    color:blue;
  }
</style>
