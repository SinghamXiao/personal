#!/usr/bin/env bash

gitPull(){
    echo "git pull"
#    git pull
}


function getDir(){
    echo "path: "$1
    cd $1
    for fileName in `ls -a $1`
        do
            if [ ${fileName} == ".git" ];then
                gitPull
            elif [ -d ${fileName} ] && [ ${fileName} != "." -a ${fileName} != ".." ];then
                getDir $1"/"${fileName}
            fi
        done
    cd ../
}

#read -p "Please input a ${path}: " ${path}

ask_yes_or_no() {
    read -p "$1 ([Y / y] yes or [N / n] no): "
    case $(echo $REPLY | tr '[A-Z]' '[a-z]') in
        Y|y|yes) echo "yes" ;;
        *)  echo "no" ;;
    esac
}

if [ "yes" != $(ask_yes_or_no "Do you want to continue? ") ]; then
    exit
else
#    getDir ${${path}}
    getDir "/home/singhamxiao/logs"
fi

