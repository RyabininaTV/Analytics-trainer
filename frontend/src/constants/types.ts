export type ITasksTypes = {
  [N: string]: string;
};

export type BackSpaceData = {
  title: string;
  url: string | ((id: number) => `/${string}`);
};

export type IActiveBackSpaceInPage = {
  [N: string]: BackSpaceData[];
};
