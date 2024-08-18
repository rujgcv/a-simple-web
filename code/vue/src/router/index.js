import { createRouter, createWebHistory } from "vue-router";
import axios from "axios";

const router = createRouter({
  history: createWebHistory(import.meta.env.BASE_URL),
  routes: [
    {
      path: "/system",
      name: "system",
      component: () => import("../views/System.vue"),
      beforeEnter: (to, from, next) => {
        axios.get("/user/userInfo").then((response) => {
          const data = response.data.data;
          console.log(response);
          if (response.data.result) {
            if (data.flag) {
              console.log(data.data.admin);
              if (data.data.admin) {
                next();
              } else {
                next("/");
              }
            } else {
              ElMessage.error(data.message);
              next("/");
            }
          }
        });
      },
      children: [
        {
          path: "home",
          name: "Home",
          component: () => import("../views/Home.vue"),
        },
        {
          path: "person",
          name: "person",
          component: () => import("../components/person/Person.vue"),
        },
        {
          path: "manage",
          name: "manage",
          component: () => import("../views/Manage.vue"),
          children: [
            {
              path: "user",
              name: "User",
              component: () => import("../components/manage/User.vue"),
            },
            {
              path: "admin",
              name: "Admin",
              component: () => import("../components/manage/Admin.vue"),
            },
            {
              path: "book",
              name: "book1",
              component: () => import("@/components/manage/Book.vue")
            },
            {
              path: "review",
              name: "review",
              component: () => import("@/components/manage/Review.vue")
            },
            {
              path: "txt",
              name: "txt",
              component: () => import("@/components/manage/BookTXT.vue")
            }
          ]
        },
        {
          path: "verify",
          name: "verify",
          component: () => import("../views/Verify.vue"),
          children: [
            {
              path: "books",
              name: "books",
              component: () => import("../components/verify/Book.vue")
            },
          ]
        },
      ],
    },
    {
      path: "/",
      name: "index",
      component: () => import("../views/IndexView.vue"),
    },
    {
      path: "/login",
      name: "login",
      component: () => import("../views/Login.vue"),
    },
    {
      path: "/search",
      name: "search",
      component: () => import("../views/Search.vue"),
    },
    {
      path: "/setting",
      name: "setting",
      component: () => import("../views/Setting.vue"),
    },
    {
      path: "/favorite",
      name: "favorite",
      component: () => import("../views/Favorite.vue"),
    },
    {
      path: "/addBook",
      name: "addBook",
      component: () => import("../views/AddBook.vue"),
    },
    {
      path: "/message",
      name: "message",
      component: () => import("../views/Message.vue")
    },
    {
      path: "/book",
      name: "book",
      component: () => import("../views/Book.vue")
    }
  ],
});

router.beforeEach((to, from, next) => {
  if (to.path === "/login") {
    next();
  } else {
    let token = localStorage.getItem("token");
    if (token === null || token === "") {
      next("/login");
    } else {
      next();
    }
  }
});

export default router;
