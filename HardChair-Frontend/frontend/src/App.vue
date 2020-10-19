<template>
  <div id="app">
    <router-view v-if="isRouterAlive"/>
  </div>
</template>

<script>
export default {
  name: 'App',
  provide(){
    return{
      reload:this.reload
    }
  },
  data(){
    return {
      isRouterAlive: true
    }
  },
  methods:{
    reload(){
      this.isRouterAlive=false;
      this.$nextTick(function(){
        this.isRouterAlive= true;
      })
    }
  }
}

</script>

<style>
  .el-button:focus, .el-button:hover {
    background: #2e48a0;
    border-color: #2e48a0;
    color: #FFF;
  }
</style>
