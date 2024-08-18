<template>
    <div style="background-color: #f9f7f7;">
        <Header class="header"></Header>
        <div class="banner"></div> 
        
        <div class="main" style="margin: 20px 140px 0 140px;">
            <el-tabs v-model="activeName" class="demo-tabs" @tab-click="handleClick">
                <el-tab-pane label="首页" name="首页"></el-tab-pane>
                <el-tab-pane label="穿越" name="穿越"></el-tab-pane>
                <el-tab-pane label="重生" name="重生"></el-tab-pane>
                <el-tab-pane label="修仙" name="修仙"></el-tab-pane>
                <el-tab-pane label="玄幻" name="玄幻"></el-tab-pane>
                <el-tab-pane label="耽美" name="耽美"></el-tab-pane>
                <el-tab-pane label="言情" name="言情"></el-tab-pane>
                <el-tab-pane label="排行榜" name="排行榜"></el-tab-pane>
                <el-tab-pane label="最新发布" name="最新发布"></el-tab-pane>
            </el-tabs>
            <IndexMain v-if="activeName=='首页'"/>
            <Box :bookType="activeName" v-if="activeName=='穿越'"/>
            <Box :bookType="activeName" v-if="activeName=='重生'"/>
            <Box :bookType="activeName" v-if="activeName=='修仙'"/>
            <Box :bookType="activeName" v-if="activeName=='玄幻'"/>
            <Box :bookType="activeName" v-if="activeName=='耽美'"/>
            <Box :bookType="activeName" v-if="activeName=='言情'"/>
            <Box :bookType="activeName" v-if="activeName=='排行榜'"/>
            <Box :bookType="activeName" v-if="activeName=='最新发布'"/>
        </div>

        <el-backtop :bottom="100">
            <div class="slide">
                <el-icon size="30px"><ArrowUp /></el-icon>
            </div>
        </el-backtop>

        <Footer></Footer>
    </div>
</template>

<script setup lang="ts">
    import { onBeforeMount, ref } from 'vue'
    import type { TabsPaneContext } from 'element-plus'
    // 标红，但是不影响
    import Header from '../components/Header.vue';
    import IndexMain from '../components/IndexMain.vue'
    import Footer from '../components/Footer.vue';
    import Box from '../components/Box.vue'
    import router from '../router';

    // date
    let activeName = ref('首页')

    const show = ref(true)

    // methods
    const handleClick = (tab: TabsPaneContext, event: Event) => {
        // console.log(activeName.value)
    }

    onBeforeMount(() => {
        document.title = '小说会'
        const token = localStorage.getItem('token')
        console.log(token);
    })
</script>

<style scoped>
    .banner {
        width: 100%;
        height: 300px;
        background-image: url("../assets/img/mf.jpg");
        /* background-size: 100% 200%; */
        background-repeat: no-repeat;
        background-position: -500px ;
        
    }
    .header{
        float: left;
        position: sticky;
        top: 0;
        z-index: 999;
    }
    .demo-tabs > .el-tabs__content {
        padding: 32px;
        color: #6b778c;
        font-size: 48px;
        font-weight: 600;
    }
    .demo-tabs {
        width: 100%;
        height: 100%;
    }
    .main::after{
        content: '';
        display: block;
        clear: both;
    }
    .slide {
        height: 100%;
        width: 100%;
        background-color: #fff;
        border: 1px solid #cdcdcd;
        border-radius: 6px;
        /* box-shadow: var(--el-box-shadow-lighter); */
        text-align: center;
        color: #a8a8a8;
        font-weight: 800px;
        line-height: 53px;  
    }
    .slide:hover {
        background-color: #1497d8;
        color: #fff;
    }
</style>