#!/usr/bin/bash

############################ GLOBALS ###########################################

API_NAME=''
DATE=''
FILE_PATH=''

HELP_FLAG=false
VERBOSE_FLAG=false
MAVEN_FLAG=false


############################ ERROR HANDLING ####################################

####################################################################
# function to exit from program if error occurred
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

############################ SCRIPT ARGUMENTS ##################################

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

  while getopts hvmf:d:a:-: OPT; do
    if [[ "$OPT" = "-" ]]; then                                                        # long option: reformulate OPT and OPTARG
      OPT="${OPTARG%%=*}"                                                              # extract long option name
      OPTARG="${OPTARG#"$OPT"}"                                                        # extract long option argument (may be empty)
      OPTARG="${OPTARG#=}"                                                             # if long option argument, remove assigning `=`
    fi
    case "$OPT" in
      h | help) HELP_FLAG=true ;;                                                      # it is impossible to specify long options with getopts
      v | verbose) VERBOSE_FLAG=true ;;                                                # it is impossible to specify long options with getopts
      m | maven) MAVEN_FLAG=true ;;                                                    # it is impossible to specify long options with getopts
      f | file_path) needs_arg "$OPTARG"; FILE_PATH="${OPTARG}" ;;                     # it is impossible to specify long options with getopts
      d | date) needs_arg "$OPTARG"; DATE="${OPTARG}" ;;                               # it is impossible to specify long options with getopts
      a | api) needs_arg "$OPTARG"; API_NAME="${OPTARG}" ;;                            # it is impossible to specify long options with getopts
      ??*) die "Illegal option --${OPT}" ;;                                            # bad long option
      \?) exit 2 ;;                                                                    # bad short option (error reported via getopts)
    esac
  done
}

function file_path_relative_to_absolute() {
  FILE_PATH=$(readlink -f "${FILE_PATH}")
  [ -f "${FILE_PATH}" ]
}


############################ POSTGRESQL ########################################

####################################################################
# function to check if database exists
####################################################################
function test_if_database_exists() {
  psql -lqt | cut -d'|' -f 1 | grep -qw "$1"
}

function try_create_database() {
    if (psql -f ./sql/DatabaseCreation.sql) >&3; then
      printf "Database created successfully!" >&1
    else
      return 1
    fi
}




############################ JAVA ###############################################

####################################################################
# function to run java app
####################################################################
function run_java_app() {
  java -jar pinfo/target/pinfo-1.0.jar \
  -Dcommand="${API_NAME}" \
  -Dfile_path="${FILE_PATH}" \
  -Ddate="${DATE}" >&3
}

####################################################################
# function to clean, compile and package java app with maven
####################################################################
function package_java_app() {
  printf "Starting packaging java app..."
  mvn -f pinfo/ clean compile package || exit_if_error $? "Can't package java project..."
  printf "Java project successfully packaged!"
}


############################ FLAG CHECK #########################################

####################################################################
# function to redirect output 3 (verbose flag)
####################################################################
function redirect_output() {
  if [ "${VERBOSE_FLAG}" = true ]; then
    exec 3>&1
  else
    exec 3>/dev/null
  fi
}


############################ SCRIPT ############################################
parse_parameters "$@" || exit_if_error $? "Can't parse command line arguments"
if "${HELP_FLAG}"; then print_help_message; exit 0; fi;
redirect_output || exit_if_error $? "Can't redirect output"

file_path_relative_to_absolute || exit_if_error $? "File with stations doesn't exist"

try_create_database || exit_if_error $? "Can't create database ${DATABASE_NAME}"

if "${MAVEN_FLAG}"; then package_java_app; fi;

run_java_app;
