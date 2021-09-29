#!/bin/bash

function isInstalled {
        if yum list installed "$@" >/dev/null 2>/dev/null; then
                true
        else
                false
        fi
}

function tryInstall {
        if isInstalled "$@"; then
                echo "  $@ already installed"
        else
                echo "  $@ is not installed"
                echo "  installing $@"
                if [ "$verbose" = true ]; then
                        sudo yum -y install $@ >/dev/null 2>/dev/null;
                else
                        sudo yum -y install $@
                fi
                if isInstalled $@; then echo "  $@ successfully installed"; else echo "  error occured while installing $@"; fi
        fi
}

while getopts v flag
do
        case "${flag}"  in
                v) verbose=true;;
        esac
done

tryInstall git;
tryInstall java-1.8.0-openjdk;
tryInstall maven;
tryInstall postgresql;