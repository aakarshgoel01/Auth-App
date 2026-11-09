import React from "react";
import { Button } from "./ui/button";
import { GithubIcon } from "./ui/github";
import { NavLink } from "react-router";
import { Globe } from "lucide-react";

function OAuth2Buttons() {
  return (
    <div className="space-y-3">
      <NavLink
        to={`${
          import.meta.env.VITE_BASE_URL || "http://localhost:8080"
        }/oauth2/authorization/google`}
        className={"block"}
      >
        <Button
          type="button"
          variant="outline"
          className="w-full cursor-pointer flex items-center gap-3 rounded-2xl"
        >
          <Globe className="w-5 h-5" /> Continue with Google
        </Button>
      </NavLink>

      <NavLink
        to={`${
          import.meta.env.VITE_BASE_URL || "http://localhost:8080"
        }/oauth2/authorization/github`}
        className={"block"}
      >
        <Button
          type="button"
          variant="outline"
          className="w-full flex cursor-pointer items-center gap-3 rounded-2xl"
        >
          <GithubIcon className="w-5 h-5" /> Continue with GitHub
        </Button>
      </NavLink>
    </div>
  );
}

export default OAuth2Buttons;