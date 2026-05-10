import {
  createBrowserRouter,
  createRoutesFromElements,
  Route,
} from "react-router-dom";
import MainLayout from "../layouts/mainLayout/mainLayout";
import { AuthPage } from "../pages/authPage";
import { TrainersList } from "../pages/trainersList";
import { TrainerTasksList } from "../pages/trainerTasksList";
import { TaskPage } from "../pages/taskPage";
import { NotFound } from "../pages/notFound";

export const router = createBrowserRouter(
  createRoutesFromElements(
    <>
      <Route path="/" element={<MainLayout />}>
        <Route index element={<TrainersList />} />
        <Route
          path="trainers/:trainerId/tasks"
          element={<TrainerTasksList />}
        />
        <Route path="tasks/:taskId" element={<TaskPage />} />
      </Route>
      <Route path="login" element={<AuthPage />} />
      <Route path="registration" element={<AuthPage />} />
      <Route path="*" element={<NotFound />} />
    </>,
  ),
);
