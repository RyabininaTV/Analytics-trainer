import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
} from "react-router-dom";
import MainLayout from "../layouts/mainLayout/mainLayout";
import { AuthPage } from "../pages/authPage";
import { TrainersList } from "../pages/trainersList";

export const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<TrainersList />} />
        <Route path="trainers/:slug" element={<h1>trainer №...</h1>} />
      </Route>
      <Route path="login" element={<AuthPage />} />
      <Route path="registration" element={<AuthPage />} />
    </>,
  ),
);
