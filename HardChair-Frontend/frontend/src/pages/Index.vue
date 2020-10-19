<template>
  <div id="base_application" v-title data-title="ArkChair - Welcome">
    <navbar></navbar>

    <section class="bg-info text-light p-0 o-hidden">
      <img src="../assets/img/rhodes.png" alt="Image" class="bg-image blend-mode-multiply" />
      <div class="container layer-2">
        <div class="row py-6 min-vh-80 align-items-center">
          <div class="col-lg-9 col-xl-8">
            <h1 class="display-1">ArkChair</h1>
            <p class="lead mb-4">The conference hosting site for tomorrow's pioneers.</p>
            <router-link to="conference-application">
              <a class="btn btn-primary btn-lg">Apply now</a>
            </router-link>
            <router-link to="conference-home">
              <a class="btn btn-primary btn-lg">See all conferences</a>
            </router-link>
            
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
  name: "Index",
  components: { navbar, footerbar },
  data(){
    return{
    }
  },
  methods:{
  }
};
</script>
