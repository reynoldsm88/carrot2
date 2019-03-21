#!/bin/sh

# Determine installation home
if [ -z "$DCS_HOME" ]; then
  DCS_HOME=`dirname "$0"`
  cd "$DCS_HOME"
  export DCS_HOME="`pwd`"
  cd - >/dev/null
fi

# Set other non-default options, if not set by the user.
if [ -z "$DCS_OPTS" ]; then
  DCS_OPTS=""
fi

# Convert to real Windows paths from Unix-ish environments on Windows
# because the java process will be Windows-native.
SHELLNAME=`uname`
case "$SHELLNAME" in
  CYGWIN*)
    DCS_HOME=`cygpath -m "$DCS_HOME"`
    echo "> DCS expects Windows paths in descriptors and arguments (Unix'ish shell detected: $SHELLNAME)"

    if [ -t 1 ]; then
      # Java doesn't see CygWin as a console, so force it.
      DCS_OPTS="$DCS_OPTS -Dconsole.updateable=true"
    fi
  ;;
  MINGW*)
    echo "> [WARNING] No support for mingw, sorry. Use CygWin cmd.exe directly."
    exit 1
  ;;
esac

# Try to determine terminal size.
if [ -z "$COLUMNS" ]; then
  COLUMNS=$(stty size 2>/dev/null)
  if [ ! -z "$COLUMNS" ]; then
     export COLUMNS=$( echo "$COLUMNS" | cut -d" " -f 2 )
  fi
fi

# Collect and quote each arg. 
all_args=
for arg in "$@" ; do
  all_args="$all_args \"$arg\""
done

# Launch DCS.
eval java $DCS_OPTS -jar \"$DCS_HOME/lib/carrot2-dcs-launcher-@version@.jar\" "$all_args"
exit $?