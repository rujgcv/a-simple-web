<template>
    <div class="main">
        <Header class="header"></Header>
        <person class="person"></person>
    </div>
</template>

<script setup>
    import { onBeforeMount } from 'vue';
    import Header from '../components/Header.vue';
    import Person from '../components/person/Person.vue';

    onBeforeMount(()=>{
        document.title = '个人设置 -小说会'
    })
</script>

<style scoped>
    .header{
        z-index: 999;
    }
    .main{
        width: 100%;
        height: 100%;
        /* background-color: #f6f6f6; */
        /* background-color: #7fd7ea; */
        background-image: linear-gradient(to bottom,#7fd7ea,#FFFDE4);
        position: relative;
    }
    .person{
        width: 200px;
        height: 0px;
        z-index: 2;
        margin-top: -2px;
        margin-left: 100px;
        position: absolute;
        top: 70px;
    }
</style>