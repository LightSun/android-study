
#NDK中某些文件夹名称也有一些区别：例如32位NDK中文件夹名称为“linux-x86”而64位NDK中文件夹名称为“linux-x86_64”。
export TMPDIR=D:/ffmpeg/tmpdir
export NDK=D:/android/android-ndk-r12b-windows-x86_64/android-ndk-r12b
export TOOLCHAIN=$NDK/toolchains/arm-Linux-androideabi-4.9/prebuilt/windows-x86_64
export SYSROOT=$NDK/platforms/android-16/arch-arm  
#export PREFIX=../264fflib  
export PREFIX=D:/ffmpeg/install_264aac 
export CPU=arm
build_one(){  
  ./configure \
--target-os=linux \
--prefix=$PREFIX \  
--enable-cross-compile \  
--enable-runtime-cpudetect \  
--disable-asm \  
--arch=$CPU \  
--cc=$TOOLCHAIN/bin/arm-linux-androideabi-gcc \  
--cross-prefix=$TOOLCHAIN/bin/arm-linux-androideabi- \
--disable-stripping \  
--nm=$TOOLCHAIN/bin/arm-linux-androideabi-nm \  
--sysroot=$SYSROOT \  
--enable-gpl --enable-shared --disable-static --enable-nonfree --enable-version3 --enable-small --disable-vda --disable-iconv \  
--disable-encoders --enable-libx264 --enable-libfaac --enable-encoder=libx264 --enable-encoder=libfaac \  
--disable-muxers --enable-muxer=mov --enable-muxer=ipod --enable-muxer=psp --enable-muxer=mp4 --enable-muxer=avi \  
--disable-decoders --enable-decoder=aac --enable-decoder=aac_latm --enable-decoder=h264 --enable-decoder=mpeg4 \  
--disable-demuxers --enable-demuxer=h264 --enable-demuxer=avi --enable-demuxer=mpc --enable-demuxer=mov \  
--disable-parsers --enable-parser=aac --enable-parser=ac3 --enable-parser=h264 \  
--disable-protocols --enable-protocol=file \  
--disable-bsfs --enable-bsf=aac_adtstoasc --enable-bsf=h264_mp4toannexb \  
--disable-indevs --enable-zlib \  
--disable-outdevs --disable-ffprobe --disable-ffplay --disable-ffmpeg --disable-ffserver --disable-debug \  
--extra-cflags="-I ../android-lib/include -fPIC -DANDROID -D__thumb__ -mthumb -Wfatal-errors -Wno-deprecated -mfloat-abi=softfp -marm -march=armv7-a" \  
--extra-ldflags="-L ../android-lib/lib"  
  
}  
  
build_one  

make clean  
make  
make install  
  
cd ..  