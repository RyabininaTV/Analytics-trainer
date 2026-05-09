import type { IActiveBackSpaceInPage, ITasksTypes } from "./types";

export const PUBLIC_ENDPOINTS = ["auth/login", "auth/register"];

export const tasksTypes: ITasksTypes = {
  ERROR_FIND: "Поиск ошибки",
  OPEN: "Открытый",
  TEST: "Тест",
};

export const activeBackSpaceInPage: IActiveBackSpaceInPage = {
  trainersPage: [
    {
      title: "Тесты",
      url: "/",
    },
  ],
  taskPage: [
    {
      title: "Задачи",
      url: (id) => `/trainers/${id}/tasks`,
    },
    {
      title: "Тесты",
      url: "/",
    },
  ],
};

export type IActiveBackSpaceInPageKeys = keyof IActiveBackSpaceInPage;
