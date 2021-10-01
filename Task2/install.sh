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
		if [ "$verbose_flag" = true ]; then
			sudo yum -y install $@ >/dev/null 2>/dev/null;
		else
			sudo yum -y install $@
		fi
		if isInstalled $@; then echo "  $@ successfully installed"; else echo "  error occured while installing $@"; fi
	fi
}

####################################################################
# function to set parameters to variables 
####################################################################
#die() { echo "$*" >&2; exit 2; }
#needs_arg() { if [ -z "$OPTARG" ]; then die "No arg for --$OPT option"; fi; }

function setParameters() {
	die() { echo "$*" >&2; exit 2; }
	needs_arg() { if [ -z "$1" ]; then die "No arg for --$OPT option"; fi; }

	OPTIND=1
	help_flag=false
	verbose_flag=false
	username=""
	gpg_key_id=""
	ssh_key_passphrase=""
	email=""
	name=""
	
	while getopts hvu:g:s:e:n:-: OPT; do
		if [[ "$OPT" = "-" ]]; then		# long option: reformulate OPT and OPTARG
			OPT="${OPTARG%%=*}"		# extract long option name
			OPTARG="${OPTARG#$OPT}"		# extract long option argument (may be empty)
			OPTARG="${OPTARG#=}"		# if long option argument, remove assigning `=`
		fi
		case "$OPT" in
			h | help)			help_flag=true ;;
			v | verbose)			verbose_flag=true ;;
			u | username)			needs_arg "$OPTARG"; username="$OPTARG" ;;
			e | email)			needs_arg "$OPTARG"; email="$OPTARG" ;;
			n | name)			needs_arg "$OPTARG"; name="$OPTARG" ;;
			g | gpg-key-id)			needs_arg "$OPTARG"; gpg_key_id="$OPTARG" ;;
			s | ssh-key-passphrase)		needs_arg "$OPTARG"; ssh_key_passphrase="$OPTARG" ;;
			??*)				die "Illegal option --$OPT" ;;  # bad long option
			\?)				exit 2 ;;  # bad short option (error reported via getopts)
		esac
	done
}


setParameters $@;
# check for help_message
if $help_flag; then
	printHelpMessage;
	exit 0;
fi
# try install packages

# git installation
tryInstall git;
# ssh configuration
if [[ ! -z "$username" && ! -z "$ssh_key_passphrase" ]]; then
	echo "Start configuring git ssh"
	echo "$name"
	if [ "$verbose_flag" = true ]; then
		eval `ssh-agent` >/dev/null;
		./git_configure_password.exp "$username" "$ssh_key_passphrase" >/dev/null;
	else
		eval `ssh-agent`
		./git_configure_password.exp "$username" "$ssh_key_passphrase"
	fi
	if [[ ! -z "$email" && ! -z "$name" ]]; then
		sudo -i -u "$username" git config --global user.email "$email"
                sudo -i -u "$username" git config --global user.name "$name"
	fi
	echo "Git ssh configured"
fi
#gpg configuration
if [[ ! -z "$gpg_key_id" ]]; then
	# gpg_key=gpg --armor --export "$gpg_key_id"
	echo "Start configuring git gpg"
	sudo -i -u "$username" git config --global user.signingkey "$gpg_key_id"
	sudo -i -u "$username" git config --global commit.gpgsign true
	echo "Git gpg configured"
fi

tryInstall java-1.8.0-openjdk;
tryInstall maven;
tryInstall postgresql-server;
# postgresql configuration
echo "Start configuring postgresql"
sudo postgresql-setup --initdb
sudo systemctl enable postgresql.service
sudo systemctl start postgresql.service

sudo -i -u postgres createuser "${username}" --createdb
sudo -i -u postgres psql -c "create database ${username} with owner ${username};"
echo "Postgresql configured"





