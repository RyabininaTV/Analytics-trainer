export type ITasksTypes = {
  [N: string]: string;
};

export type BackSpaceData = {
  title: string;
  url: string | ((id: number) => `/${string}`);
};

export type BackSpaceLinks = {
  matchLink: RegExp;
  goBack: Array<BackSpaceData>;
};
