import type { BackSpaceLinks, ITasksTypes, UserStatusType } from "./types";

export const PUBLIC_ENDPOINTS = ["auth/login", "auth/register"];

export const tasksTypes: ITasksTypes = {
  ERROR_FIND: "Поиск ошибки",
  OPEN: "Открытый",
  TEST: "Тест",
};

export const backSpaceLinks: BackSpaceLinks[] = [
  {
    matchLink: /^\/trainers\/\d+\/tasks$/,
    goBack: [
      {
        title: "Тесты",
        url: "/",
      },
    ],
  },
  {
    matchLink: /^\/tasks\/\d+$/,
    goBack: [
      {
        title: "Задачи",
        url: (id) => `/trainers/${id}/tasks`,
      },
      {
        title: "Тесты",
        url: "/",
      },
    ],
  },
];

export const userStatus: UserStatusType = {
  ACTIVE: "Активен",
  BLOCKED: "Заблокирован",
};
