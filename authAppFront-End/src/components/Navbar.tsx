import React from "react";
import { Button } from "./ui/button";
import { NavLink, useNavigate } from "react-router";

function Navbar() {
    return (
        <nav className="flex h-14 justify-around items-center dark:text-white">
            <div className="font-semibold text-lg items-center flex gap-2">
                <span className="inline-block text-center h-6 w-6 rounded-md bg-gradient-to-r from-primary to-primary/40">A</span>
                <NavLink to="/">
                <span>Auth App</span>
                </NavLink>
            </div>
            <div className="flex gap-4 items-center">
                <NavLink to="/">Home</NavLink>
                <NavLink to="/login">
                    <Button size="sm" className="cursor-pointer" variant="outline">
                        Login
                    </Button>
                </NavLink>
                <NavLink to="/signup">
                    <Button size="sm" className="cursor-pointer" variant="outline">
                        Sign Up
                    </Button>
                </NavLink>
                <NavLink to="/services">
                    <Button size="sm" className="cursor-pointer" variant="outline">
                        Services
                    </Button>
                </NavLink>
                <NavLink to="/about">
                    <Button size="sm" className="cursor-pointer" variant="outline">
                        About
                    </Button>
                </NavLink>
            </div>
        </nav>
    );
}

export default Navbar;