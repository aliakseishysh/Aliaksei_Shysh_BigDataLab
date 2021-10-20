#!/bin/bash
#
# Task 4 deployment script

####################################################################
# function to print help message
####################################################################
function print_help_message() {
  echo "Script for Task 4 deployment"
  echo "Usage:"
  echo "  ./run_queries.sh [OPTIONS]"
  echo "Options:"
  echo "  -h/--help                    OPTIONAL. Echo help message,\
 (no arguments required)"
  echo "  -v/--verbose                 OPTIONAL. Run script in \
 verbose mode (only script info), (no arguments required)"
  echo "  -f/--save-to-file"
  echo "  -q/--query-name              OPTIONAL. Run script with \
 specified query. Example: '-q 2435345' (2435345 - query index)"
  echo "  -s/--start-month"
  echo "  -e/--end-month"
  echo "  -r/--rows-count"
  echo "  1) ./run_queries.sh -q 1 -s 2021-01 -e 2021-02"
}

############################ GLOBALS ###########################################

QUERY_NAME=''
START_MONTH=''
END_MONTH=''

ROWS_COUNT=10

HELP_FLAG=false
VERBOSE_FLAG=false
SAVE_TO_FILE=''

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
# shellcheck disable=SC2214
function parse_parameters() {
  function die() { echo "$*" >&2; exit 2; }
  function needs_arg() {
    if [ -z "$1" ]; then
      die "No arg for --$OPT option";
    fi;
  }

  OPTIND=1

  while getopts hvq:s:e:r:f:-: OPT; do
    if [[ "$OPT" = "-" ]]; then                                                        # long option: reformulate OPT and OPTARG
      OPT="${OPTARG%%=*}"                                                              # extract long option name
      OPTARG="${OPTARG#"$OPT"}"                                                        # extract long option argument (may be empty)
      OPTARG="${OPTARG#=}"                                                             # if long option argument, remove assigning `=`
    fi
    case "$OPT" in
      h | help) HELP_FLAG=true ;;
      v | verbose) VERBOSE_FLAG=true ;;
      q | query-name) needs_arg "$OPTARG"; QUERY_NAME="${OPTARG}" ;;
      s | start-month) needs_arg "$OPTARG"; START_MONTH="${OPTARG}" ;;
      e | end-month) needs_arg "$OPTARG"; END_MONTH="${OPTARG}" ;;
      r | rows-count) needs_arg "$OPTARG"; ROWS_COUNT="${OPTARG}" ;;
      f | save-to-file) needs_arg "$OPTARG"; SAVE_TO_FILE="${OPTARG}" ;;
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

####################################################################
# function to check if postgresql service is active
####################################################################
function check_if_postgresql_is_active() {
  systemctl is-active --quiet postgresql
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
#  if [ -n "${SAVE_TO_FILE}" ]; then
#    exec 4>"${SAVE_TO_FILE}"
#  else
#    exec 4>&1
#  fi

}
############################ SQL QUERIES #######################################

function run() {

  if [ -n "${QUERY_NAME}" ]; then
      if [ "${QUERY_NAME}" = '1' ]; then
        psql -d task_4 -v start_month=\'"${START_MONTH}-01"\' -v end_month=\'"${END_MONTH}-01"\' -f ./sql/queries/"${QUERY_NAME}".sql >"${SAVE_TO_FILE}"
        column -t "${SAVE_TO_FILE}" -s ":" | sed -n 1,$((ROWS_COUNT+2))p
      fi
    else
      exit 1
  fi
}


############################ SCRIPT ############################################

parse_parameters "$@" || exit_if_error $? "Can't parse command line arguments"
if "${HELP_FLAG}"; then print_help_message; exit 0; fi;
redirect_output || exit_if_error $? "Can't redirect output"

check_if_postgresql_is_active || exit_if_error $? "Postgresql service is not active"

run