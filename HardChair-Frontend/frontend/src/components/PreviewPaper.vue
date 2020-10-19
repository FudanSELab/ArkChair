<template>
<div>
 <el-button type="primary" @click="preview">Preview</el-button>
 <el-dialog :visible.sync="dialogVisible" width="80%" top="20px">
    <div style="height: 450px">
      <iframe :src="pdfUrl" title="Preview" style="width: 100%; height: 100%"></iframe>
    </div>
 </el-dialog>
</div>
</template>

<script>

export default {
  name: "PreviewPaper",
  props:{
    id:{
        type:[String,Number],
        required:true
    }
  },
  data(){
      return{
          dialogVisible:false,
          pdfUrl:""
      }
  },
  methods:{
      preview(){
      this.$axios({
        method:'post',
        url:'/DownloadPaper',
        data:{paperId:this.id},
        responseType: 'blob'
      })
      .then(resp=>{
        this.dialogVisible = true;
        this.pdfUrl=URL.createObjectURL(new Blob([resp.data],{type:'application/pdf'}));
      })
      .catch(error =>{
        console.log(error);
      })
    },
  }
};
</script>

<style>
.onPageBtn {
  margin-left: 12px;
}
</style>
