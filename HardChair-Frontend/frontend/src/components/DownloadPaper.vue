<template>
<div>
 <el-button type="primary" @click="download">Download</el-button>
</div>
</template>

<script>

export default {
  name: "DownloadPaper",
  props:{
    id:{
        type:[String,Number],
        required:true
    },
    title:{
        type:[String],
        required:true
    }
  },
  methods:{
    download(){
      this.$axios({
        method:'post',
        url:'/DownloadPaper',
        data:{paperId:this.id},
        responseType: 'blob'
      })
      .then(resp=>{
        const blob = new Blob([resp.data]);
        const fileName = this.title+'.pdf';
        if (typeof navigator.msSaveBlob != "undefined") { // IE10+下载
          navigator.msSaveBlob(blob, fileName);
        } else { // 非IE下载
          const elink = document.createElement('a');
          elink.href = URL.createObjectURL(blob);
          elink.download = fileName;
          elink.style.display = 'none';

          document.body.appendChild(elink);
          elink.click();
          URL.revokeObjectURL(elink.href); // 释放URL 对象
          document.body.removeChild(elink);
        }
      })
      .catch(error=>{
        console.log(error);
      })
    }
  }
};
</script>

<style>
.onPageBtn {
  margin-left: 12px;
}
</style>
