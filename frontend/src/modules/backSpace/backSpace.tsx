import { useLocation } from "react-router-dom";
import { BackSpaceView } from "../../components/backSpaceView";
import { useEffect } from "react";
import { activeBackSpaceInPage } from "../../constants/constants";

const BackSpace = () => {
  const { pathname } = useLocation();

  useEffect(() => {
    console.log("pathname: ", pathname);
  }, [pathname]);

  return <BackSpaceView linksList={activeBackSpaceInPage["taskPage"]} />;
};

export default BackSpace;
