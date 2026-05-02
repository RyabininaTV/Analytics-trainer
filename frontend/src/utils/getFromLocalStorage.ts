export const getFromLocalStorage = <T>(name: string): T | null => {
  const dataInLocalStorage = localStorage.getItem(name);
  const data: T | null = dataInLocalStorage
    ? JSON.parse(dataInLocalStorage)
    : null;

  return data;
};
