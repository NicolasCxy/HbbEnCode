cmake_minimum_required(VERSION 3.10.2)
project("hbb_x264")

#导入当前目录下所有.cpp文件
file(GLOB cpp_source  *.cpp)

#导入X264头文件和动态库
include_directories(${CMAKE_SOURCE_DIR}/x264/${ANDROID_ABI}/include)

set(CMAKE_CXX_FLAGS "${CMAKE_CXX_FLAGS} -L${CMAKE_SOURCE_DIR}/x264/${ANDROID_ABI}/lib")


# 这一句用来导出编译生成的so库
set(CMAKE_LIBRARY_OUTPUT_DIRECTORY ${PROJECT_SOURCE_DIR}/jniLibs/${ANDROID_ABI})



add_library(
        native-lib
        SHARED
        ${cpp_source})

target_link_libraries(
        native-lib
        x264
        log)