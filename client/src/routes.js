// import Dashboard from "views/Dashboard.js";
import Icons from "views/Icons.js";
import Map from "views/Map.js";
import Notifications from "views/Notifications.js";
import Rtl from "views/Rtl.js";
import TableList from "views/TableList.js";
import Typography from "views/Typography.js";
import UserProfile from "views/UserProfile.js";
import History from "views/History.js";
import Chart from "react-chartjs-2";
import Charts from "views/Charts";
import DashboardTable from "views/DashboardTable";
import ProjectData from "views/ProjectData";

var routes = [
  {
    path: "/dashboard",
    name: "Dashboard",
    rtlName: "لوحة القيادة",
    icon: "tim-icons icon-chart-pie-36",
    component: <DashboardTable />,
    layout: "/admin",
  },
  {
    path: "/history",
    name: "History",
    rtlName: "الرموز",
    icon: "tim-icons icon-calendar-60",
    component: <History />,
    layout: "/admin",
  },
  {
    path: "/Charts",
    name: "Charts",
    rtlName: "خرائط",
    icon: "tim-icons icon-chart-bar-32",
    component: <Charts />,
    layout: "/admin",
  },
  {
    path: "/notifications",
    name: "Notifications",
    rtlName: "إخطارات",
    icon: "tim-icons icon-bell-55",
    component: <Notifications />,
    layout: "/admin",
  },
  {
    path: "/user-profile",
    name: "User Profile",
    rtlName: "ملف تعريفي للمستخدم",
    icon: "tim-icons icon-single-02",
    component: <UserProfile />,
    layout: "/admin",
  },
  {
    path: "/project/:id",
    name: "Project Data",
    icon: "tim-icons icon-archive", // You can choose an appropriate icon
    component: <ProjectData />,
    layout: "/admin",
  },
  // {
  //   path: "/tables",
  //   name: "Table List",
  //   rtlName: "قائمة الجدول",
  //   icon: "tim-icons icon-puzzle-10",
  //   component: <TableList />,
  //   layout: "/admin",
  // },
];
export default routes;
