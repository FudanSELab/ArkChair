<template>
    <span>{{message}}</span>
</template>

<script>
export default {
    name:'ParseStatus',
    props:['status'],
    computed:{
        message(){
            switch (this.status) {
              case "CHECKED":
                return "Approved by admin";
                break;
              case "SUBMIT_ALLOWED":
                return "Accepting papers";
                break;
              case "UNCHECKED":
                return "Waiting for verification";
                break;
              case "OPEN_REVIEW":
                return "Papers being reviewed"
                break;
              case "OPEN_RESULT":
                return "Result has been announced"
                break;
              case "OPEN_FINAL_RESULT":
                return "Final result has been annouced"
                break;
              default:
                return "Currently unknown";
                break;
            }
        }
    }    
}
</script>