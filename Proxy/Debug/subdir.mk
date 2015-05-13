################################################################################
# Automatically-generated file. Do not edit!
################################################################################

# Add inputs and outputs from these tool invocations to the build variables 
CPP_SRCS += \
../BTesti.cpp \
../BluetoothConnection.cpp \
../BluetoothManager.cpp \
../MVision.cpp \
../Main.cpp \
../PlayDirector.cpp \
../Robot.cpp \
../Server.cpp \
../Stage.cpp \
../StageCorner.cpp \
../StagePoint.cpp \
../Testi.cpp 

OBJS += \
./BTesti.o \
./BluetoothConnection.o \
./BluetoothManager.o \
./MVision.o \
./Main.o \
./PlayDirector.o \
./Robot.o \
./Server.o \
./Stage.o \
./StageCorner.o \
./StagePoint.o \
./Testi.o 

CPP_DEPS += \
./BTesti.d \
./BluetoothConnection.d \
./BluetoothManager.d \
./MVision.d \
./Main.d \
./PlayDirector.d \
./Robot.d \
./Server.d \
./Stage.d \
./StageCorner.d \
./StagePoint.d \
./Testi.d 


# Each subdirectory must supply rules for building sources it contributes
%.o: ../%.cpp
	@echo 'Building file: $<'
	@echo 'Invoking: GCC C++ Compiler'
	g++ -I/usr/local/include -I/usr/local/include/opencv -O0 -g3 -Wall -c -fmessage-length=0 -MMD -MP -MF"$(@:%.o=%.d)" -MT"$(@:%.o=%.d)" -o "$@" "$<"
	@echo 'Finished building: $<'
	@echo ' '


