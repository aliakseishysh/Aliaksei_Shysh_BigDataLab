#!/bin/bash
#
# Performs java, maven, git and postgresql installation
#
# Error codes:
#   3 - Configuration error
#   4 - Installation error
#


####################################################################
# function to print help message
####################################################################
function print_help_message() {
  echo "Script for installing and configurating git, java-1.8.0-openjdk, 
  maven and postgresql packages"
  echo "Usage:"
  echo "  bash install.sh [options]"
  echo "Options:"
  echo "  -h/--help                    echo help message"
  echo "  -v/--verbose                 run script in verbose mode (only script\
 info)"
  echo "  -u/--username                username to configure tools for"
  echo "  -e/--email                   user email (git ssh configuration)"
  echo "  -n/--name                    user name (git ssh configuration)"
  echo "  -g/--gpg-key-id              user gpg key id (git gpg configuration)"
  echo "  -s/--ssh-key-passphrase      user ssh passphrase (git ssh\
configuration)"
}


############################ ERROR HANDLING ####################################

####################################################################
# function to exit from programm if error occured
####################################################################
function exit_if_error() {
  local exit_code=$1
  shift
  [[ $exit_code ]] &&
    ((exit_code != 0)) && {
      printf 'ERROR: %s\n' "$@">&2
      exit "$exit_code"
    }
}

############################ MAIN FUNCTIONS ####################################

####################################################################
# function to check if package is installed
# ARGUMENTS:
#   Package name to check
# RETURN:
#   true if package is installed, false otherwise
####################################################################
function is_installed() {
  if yum list installed "$@" 1>/dev/null 2>/dev/null; then
    true
  else
    false
  fi
}

####################################################################
# function to try install package or skip installation 
# if it is already installed
# ARGUMENTS:
#   Package name to try install
####################################################################
function try_install() {
  if is_installed "$@"; then
    echo "$* already installed"
  else
    echo "$* is not installed"
    echo "installing $*..."
    if [ "$verbose_flag" = false ]; then
      sudo yum -y install "$@" 1>/dev/null 2>/dev/null;
    else
      sudo yum -y install "$@"
    fi
    if is_installed "$@"; then 
      echo "$* successfully installed!"; 
    else 
      echo "error occured while installing $*";
      return 4 
    fi
  fi
}

####################################################################
# function to set parameters to variables 
####################################################################
function parse_parameters() {
  function die() { echo "$*" >&2; exit 2; }
  function needs_arg() { 
    if [ -z "$1" ]; then 
      die "No arg for --$OPT option"; 
    fi; 
  }

  OPTIND=1
  help_flag=false
  verbose_flag=false
  username=""
  gpg_key_id=""
  ssh_key_passphrase=""
  email=""
  name=""
	
  while getopts hvu:g:s:e:n:-: OPT; do
    if [[ "$OPT" = "-" ]]; then                                                        # long option: reformulate OPT and OPTARG
      OPT="${OPTARG%%=*}"                                                              # extract long option name
      OPTARG="${OPTARG#"$OPT"}"                                                        # extract long option argument (may be empty)
      OPTARG="${OPTARG#=}"                                                             # if long option argument, remove assigning `=`
    fi
    case "$OPT" in
      h | help) help_flag=true ;;                                                      # it is impossible to specify long options with getopts
      v | verbose) verbose_flag=true ;;                                                # it is impossible to specify long options with getopts
      u | username) needs_arg "$OPTARG"; username="${OPTARG}" ;;                       # it is impossible to specify long options with getopts
      e | email) needs_arg "$OPTARG"; email="${OPTARG}" ;;                             # it is impossible to specify long options with getopts
      n | name) needs_arg "$OPTARG"; name="${OPTARG}" ;;                               # it is impossible to specify long options with getopts
      g | gpg-key-id) needs_arg "$OPTARG"; gpg_key_id="${OPTARG}" ;;                   # it is impossible to specify long options with getopts
      s | ssh-key-passphrase)                                                          # it is impossible to specify long options with getopts
                              needs_arg "$OPTARG"; 
                              ssh_key_passphrase="${OPTARG}" ;;   
      ??*) die "Illegal option --${OPT}" ;;  # bad long option
      \?) exit 2 ;;  # bad short option (error reported via getopts)
    esac
  done
}

############################ CONFIGURATION #####################################

####################################################################
# function to configure git ssh
####################################################################
function configure_git_ssh() {
  if [[ -n "${username}" && -n "${ssh_key_passphrase}" ]]; then
    echo "Start configuring git ssh..."
    command_string="eval \$(ssh-agent); export SSH_AUTH_SOCK=${SSH_AUTH_SOCK}; \
    export SSH_AGENT_PID=${SSH_AGENT_PID}; expect git_configure_password.exp \
    ${username} ${ssh_key_passphrase}"
    if [ "${verbose_flag}" = false ]; then
      if ! (sudo -i -u "${username}" \
	      bash -c "${command_string}" 1>/dev/null); then return 3; fi;
    else
      if ! (sudo -i -u "${username}" \
	      bash -c "${command_string}" ); then return 3; fi;
    fi   
    echo "Git ssh configured successfully!"
  fi
}

####################################################################
# function to configure git globals
####################################################################
function configure_git_globals() {
  if [[ -n "${username}" && -n "${email}" && -n "${name}" ]]; then
    echo "Start configuring git globals.."
    if [ "${verbose_flag}" = false ]; then
      if ! (sudo -i -u "${username}" git config --global user.email "${email}" &&
      sudo -i -u "${username}" git config --global user.name "${name}" \
      1>/dev/null); then return 3; fi;
    else
      if ! (sudo -i -u "${username}" git config --global user.email "${email}" &&
      sudo -i -u "${username}" git config --global user.name  \
      "${name}"); then return 3; fi;
    fi
    echo "Git globals configured successfully!"  
  fi
}

####################################################################
# function to configure git gpg
####################################################################
function configure_git_gpg() {
  if [[ -n "${gpg_key_id}" ]]; then
    echo "Start configuring git gpg..."
    if [ "${verbose_flag}" = false ]; then
      if ! (sudo -i -u "${username}" git config --global user.signingkey \
      "${gpg_key_id}" && sudo -i -u "${username}" git config --global \
      commit.gpgsign true 1>/dev/null); then return 3; fi;
    else
      if ! (sudo -i -u "${username}" git config --global user.signingkey \
      "${gpg_key_id}" && sudo -i -u "${username}" git config --global \
      commit.gpgsign true); then return 3; fi;
    fi
    echo "Git gpg configured successfully!"
  fi
}

####################################################################
# function to create postgresql cluster
####################################################################
function configure_postgresql_create_db_cluster() {
  local directory="/var/lib/pgsql/data"
  echo "Start creating db cluster..."
  if [ ! "$(ls -A ${directory})" ]; then
    if [ "${verbose_flag}" = false ]; then
      if ! (sudo postgresql-setup --initdb 1>/dev/null); then return 3; fi;
    else
      if ! (sudo postgresql-setup --initdb); then return 3; fi;
    fi
  else
    echo "Directory ${directory} is not empty, please, delete and restart\
 script"
    return 3
  fi
  echo "Database cluster created successfully!"
}

####################################################################
# function to enable and start postgresql service
####################################################################
function configure_postgresql_service() {
  echo "Start enabling and starting postgresql service..."
  if [ "${verbose_flag}" = false ]; then
    if ! (sudo systemctl enable postgresql.service --now 1>/dev/null); then 
      return 3; 
    fi;
  else
    if ! (sudo systemctl enable postgresql.service --now); then return 3; fi;
  fi
  # test service
  if systemctl is-active --quiet postgresql; then
    echo "Postgresql service is active!"
  else
    (exit 3) && exit_if_error $? "Postgresql service is not active"
  fi
}

####################################################################
# function to create user and database
####################################################################
function configure_postgresql_user() {
  echo "Start creating user and database for postgresql..."
  if [ "${verbose_flag}" = false ]; then
    if ! (sudo -i -u postgres createuser "${username}" --createdb &&
    sudo -i -u postgres psql -c \
    "create database ${username} with owner ${username};" 1>/dev/null); then 
      return 3; 
    fi
  else
    if ! (sudo -i -u postgres createuser "${username}" --createdb && \
    sudo -i -u postgres psql -c \
    "create database ${username} with owner ${username};"); then return 3; fi;
  fi
  echo "User and database for postgresql created successfully!"
}

############################ SCRIPT RUN ########################################


parse_parameters "$@" || exit_if_error $? "Can't parse command line arguments"
if "${help_flag}"; then print_help_message; exit 0; fi;

try_install git || exit_if_error $? "Can't install git";
try_install java-1.8.0-openjdk || exit_if_error $? "Can't install java";
try_install maven || exit_if_error $? "Can't install maven";
try_install postgresql-server || exit_if_error $? "Can't install postgresql";

configure_git_ssh || exit_if_error $? "Can't configure git ssh";
configure_git_globals || exit_if_error $? "Can't configure git globals";
configure_git_gpg || exit_if_error $? "Can't configure git gpg";

configure_postgresql_create_db_cluster || exit_if_error $? "Can't create\
 postgresql db cluster";
configure_postgresql_service || exit_if_error $? "Can't start postgresql\
 service";
configure_postgresql_user || exit_if_error $? "Can't configure postgresql user";

echo "Complete!"
