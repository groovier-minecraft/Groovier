package commands.math

import com.ericlam.mc.groovier.command.CommandScript
import com.ericlam.mc.groovier.command.CommandArg
import groovy.transform.Field
import org.bukkit.command.CommandSender
import services.MathService

import javax.inject.Inject

// showcase for multiple commands
// and using dependency injection for service

@Field @Inject MathService mathService

@CommandScript(description = 'add two numbers')
void add(CommandSender sender, @CommandArg('one') int a, @CommandArg('two') int b) {
    sender.sendMessage("$a + $b = ${mathService.add(a, b)}")
}

