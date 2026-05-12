import { useQuery } from "@tanstack/react-query";
import { profileApi } from "../../api/authApi";

export function useGetUserProfile() {
  return useQuery({
    queryKey: ["userProfile"],
    queryFn: () => profileApi.getProfile().then((res) => res.data),
  });
}
