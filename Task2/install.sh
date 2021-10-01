#!/bin/bash


# functions


####################################################################
# function to print help message
####################################################################
function printHelpMessage {
	echo "  Script for installing and configurating git, java-1.8.0-openjdk, maven and postgresql packages"
	echo "    -h/--help : echo help message:"
	echo "    -v/--verbose : run script in verbose mode"
	echo "  Usage examples:"
	echo "    bash install.sh -h"
	echo "    bash install.sh -v"	
}


####################################################################
# function to check if package is installed
# ARGUMENTS:
#	Package name to check
# RETURN:
#	true if package is installed, false otherwise
####################################################################
function isInstalled {
	if yum list installed "$@" >/dev/null 2>/dev/null; then
		true
	else
		false
	fi
}

####################################################################
# function to try install package or skip installation 
# if it is already installed
# ARGUMENTS:
#	Package name to try install
####################################################################
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

####################################################################
# function to transform long parameters to short
# and to set shot parameters to variables 
####################################################################
function setParameters {
	# transform long parameters to short
	for arg in "$@"; do
		shift
		case "$arg" in
			"--help") set -- "$@" "-h";;
			"--verbose") set -- "$@" "-v";;
		        "--git-ssh-password") set -- "$@" "-g";;	
			*) set -- "$@" "$arg"
		esac
	done

	verbose=false;
	help_message=false;

	# set short parametes to variables
	while getopts vhg: flag
	do
		case "${flag}" 	in
			v) verbose=true;;
			h) help_message=true;;
			g) git_ssh_password="${OPTARG}";; 
		esac
	done
}




# script

setParameters;
# check for help_message
if $help_message; then
	printHelpMessage;
	exit 0;
fi
# try install packages

# git installation
tryInstall git;





tryInstall java-1.8.0-openjdk;
tryInstall maven;
tryInstall postgresql;
